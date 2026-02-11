package com.example.demo.repository;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RestrictedRepositoryImpl<T, ID, R extends RestrictedRepository<T, ID, R>>
        implements RestrictedRepository<T, ID, R> {

    private final EntityManager em;
    private final JpaEntityInformation<T, ?> info;

    public RestrictedRepositoryImpl(JpaEntityInformation<T, ?> info, EntityManager em) {
        this.info = info;
        this.em = em;
    }

    @Override
    public List<T> findAll() { return findAll(FetchGraph.of(), new PageRequestEx(0, Integer.MAX_VALUE, null), null); }

    public List<T> findAll(FetchGraph graph, PageRequestEx page, Specification<T> spec) {
        // Phase 1: IDs
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ID> cq = cb.createQuery(info.getIdType());
        Root<T> root = cq.from(info.getJavaType());
        cq.select(root.get(info.getIdAttribute()));

        if (spec != null) {
            Predicate p = spec.toPredicate(root, cq, cb);
            cq.where(p);
            if (!root.getFetches().isEmpty()) throw new IllegalStateException("Fetch joins forbidden in Specification");
        }

        applySorting(cb, cq, root, page.sort());

        List<ID> ids = em.createQuery(cq).setFirstResult(page.offset()).setMaxResults(page.size()).getResultList();
        if (ids.isEmpty()) return List.of();

        // Phase 2: fetch graph
        EntityGraph<T> eg = em.createEntityGraph(info.getJavaType());
        for (String path : graph.paths()) applyGraphPath(eg, path);

        return em.createQuery(
            "select e from " + info.getEntityName() + " e where e.id in :ids",
            info.getJavaType()
        ).setParameter("ids", ids)
         .setHint("jakarta.persistence.fetchgraph", eg)
         .getResultList();
    }

    public long count(Specification<T> spec) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(info.getJavaType());
        cq.select(cb.count(root));

        if (spec != null) cq.where(spec.toPredicate(root, cq, cb));
        return em.createQuery(cq).getSingleResult();
    }

    public List<T> findAfterCursor(FetchGraph graph, Object lastId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ID> cq = cb.createQuery(info.getIdType());
        Root<T> root = cq.from(info.getJavaType());
        cq.select(root.get(info.getIdAttribute()));
        if (lastId != null) cq.where(cb.greaterThan(root.get(info.getIdAttribute()), (Comparable) lastId));
        cq.orderBy(cb.asc(root.get(info.getIdAttribute())));
        List<ID> ids = em.createQuery(cq).setMaxResults(limit).getResultList();
        if (ids.isEmpty()) return List.of();

        EntityGraph<T> eg = em.createEntityGraph(info.getJavaType());
        for (String path : graph.paths()) applyGraphPath(eg, path);

        return em.createQuery(
            "select e from " + info.getEntityName() + " e where e.id in :ids",
            info.getJavaType()
        ).setParameter("ids", ids)
         .setHint("jakarta.persistence.fetchgraph", eg)
         .getResultList();
    }

    private void applySorting(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<T> root, SortEx sort) {
        if (sort == null) return;
        List<Order> orders = new ArrayList<>();
        for (SortEx.Order o : sort.orders()) orders.add(o.direction() == SortEx.Direction.ASC ? cb.asc(root.get(o.property())) : cb.desc(root.get(o.property())));
        cq.orderBy(orders);
    }

    @SuppressWarnings("unchecked")
    private void applyGraphPath(EntityGraph<?> root, String path) {
        String[] parts = path.split("\.");
        Subgraph<?> sub = null;
        for (String p : parts) sub = (sub == null ? root.addSubgraph(p) : sub.addSubgraph(p));
    }
}
