## Graduation Project Top Java
**Educational project from https://javaops.ru/view/topjava**

This is the 2th vertion of that project. Previous you can find in my repository by link https://github.com/gosha-kap/voting.git 

----
### The task is:
Design and implement a REST API using Hibernate/Spring/SpringMVC (or Spring-Boot) **without frontend**.

Build a voting system for deciding where to have lunch.

* 2 types of users: admin and regular users
* Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
* Menu changes each day (admins do the updates)
* Users can vote on which restaurant they want to have lunch at
* Only one vote counted per user
* If user votes again the same day:
    - If it is before 11:00 we assume that he changed his mind.
    - If it is after 11:00 then it is too late, vote can't be changed

Each restaurant provides a new menu each day.

____

### Description:

This version is based on Spring Boot. Database is H2 that running in memory. Spring Securiy provides authirization and authentication. Database shema is generated from entities and populated by script from recources.

There are two users: user and admin theirs  passwords are coinsides with logins. User can vote for restaurant menu , get vote history and edit profile. Admin can also edit restaurants, edit today menu. For voting you need only restaurant id. You can choose vote valuefrom 1 to 10.

To get rest api visit  **http://localhost:8080/swagger-ui/index.html** after start app.






