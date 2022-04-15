INSERT INTO USERS (LOGIN,FIRST_NAME, LAST_NAME, PASSWORD)
VALUES ('user','User_First', 'User_Last','{bcrypt}$2a$12$fSjh.4TrZB9Gle1rmFzIx.U1u9F5AhxU.wGVtLF9MipJARoH2DyyG'),
       ('admin','Admin_First', 'Admin_Last','{bcrypt}$2a$12$NY6uGRJHbDPyXsvSpJgmt.KJIMVrkOL0sSgbekorCD.Ti1FwzuzWe');

INSERT INTO USER_ROLES (ROLE, USER_ID)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2);

INSERT INTO  RESTAURANTS (NAME)
VALUES ( 'suchi bar' ),
       ('kafe nid'),
       ('new restaurant');

INSERT INTO MENUS (DATE, RESTAURANT_ID)
VALUES (NOW(),1),
       (NOW(),2),
       (current_date-1,2);


INSERT INTO MEALS (DESCRIPTION, PRICE)
VALUES ('zavtrak',10.45 ),
       ('obed',20.00),
       ('yzin',30.67),

 ('zavtrak2',11.45 ),
 ('obed2',21.00),
 ('yzin2',31.67);

INSERT INTO MENUS_MEALS (MENU_ID, MEALS_ID) VALUES ( 1,1 ),(1,2),(2,3),(2,4),(3,5),(3,6);

INSERT INTO VOTE (DATE, RATE, RESTAURANT_ID, USER_ID)
VALUES ( now(),5,1,1 ),
       ( CURRENT_DATE -1,7,2,1 ),
       (now(),8,1,2);