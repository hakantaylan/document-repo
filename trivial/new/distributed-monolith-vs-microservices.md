### From [You're not actually building microservices](https://www.simplethread.com/youre-not-actually-building-microservices/)

So, are you building microservices? Take a look at a few of these symptoms, and decide for yourself:

- :x: A change to one microservice often requires changes to other microservices
- :white_check_mark: Deploying one microservice requires other microservices to be deployed at the same time
- :question: Your microservices are overly chatty
- :x: The same developers work across a large number of microservices
- :white_check_mark: Many of your microservices share a datastore
- :white_check_mark: Your microservices share a lot of the same code or models

Now, I’m not saying that if your microservices implementation checks one of these boxes that you have a problem… there are always exceptions. But for the most part, if you’re nodding your head at a number of the points above, you might not be working with microservices.

...

If you start off with a monolith, your goal should be to keep it from growing into a monstrosity. The key to doing this is to simply listen to your application! I know, this is easier said than done, but as your monolith grows, keep asking yourself a few questions:

- [ ] Is there anything that is scaling at a different rate than the rest of the system?
- [ ] Is there anything that feels “tacked-on” to the outside of the system?
- [ ] Is there anything changing much faster than the rest of the system?
- [ ] Is there a part of the system that requires more frequent deploys than the rest of the system?
- [ ] Is there a part of the system that a single person, or small team, operates independently inside of?
- [ ] Is there a subset of tables in your datastore that isn’t connected to the rest of the system?

As the system size and developer count gets larger you’ll find that splitting out services will become commonplace. 

### From [Segment's Goodbye Microservices](https://segment.com/blog/goodbye-microservices/)
> To ease the burden of developing and maintaining these codebases, we created shared libraries to make common transforms and functionality, such as HTTP request handling, across our destinations easier and more uniform.

...

> The shared libraries made building new destinations quick. The familiarity brought by a uniform set of shared functionality made maintenance less of a headache.

> However, a new problem began to arise. Testing and deploying changes to these shared libraries impacted all of our destinations. It began to require considerable time and effort to maintain. Making changes to improve our libraries, knowing we’d have to test and deploy dozens of services, was a risky proposition. When pressed for time, engineers would only include the updated versions of these libraries on a single destination’s codebase.

> Over time, the versions of these shared libraries began to diverge across the different destination codebases. The great benefit we once had of reduced customization between each destination codebase started to reverse. Eventually, all of them were using different versions of these shared libraries. We could’ve built tools to automate rolling out changes, but at this point, not only was developer productivity suffering but we began to encounter other issues with the microservice architecture.


In my experience, the biggest benefit of microservices is decoupling teams.
Developer productivity is very hard to maintain in a monolithic app as the number of developers increases and the legacy code piles up. Breaking up services and giving each dev team control over their own codebases enables them to develop their own products at their own pace.

If you only have one dev team, microservices are a lot less attractive. However, there are still some benefits, such as being able to refactor parts of your codebase in isolation (including perhaps rewriting them in different languages), and the ability to individually adjust the runtime scale of different parts of your codebase.