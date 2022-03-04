# GameCenter
The application GameCenter is an application where you can play the 2048 game and the Peg Solitaire game. This game center has several android activities in addition to the Game2048Activity and PegSolitaireActivity. 

The activities that we can find in this application are: the SplashActivity, displayed when the application starts; the LoginActivity, where you has to log in with a registered user to use the application; the SignUp activity, where you can register a new user to use the application; the GamesListActivity, a RecycleView with CardViews representing each game where you can choose one of the games to start playing; the UserSettingsActivity, to change different things about the user or delete the own user;  the RecordsListActivity, where you can see all the scores that has been stored in the mobile phone and the RecordActivity, where you can see all the information of one of the RecordsListActiviy records. In addition to this Classes, there are different DialogFragments, some complicated and some simpler, and logic or back-end classes. 

Among the back-end classes there are the Table2048 and TablePegSolitaire, where there is the logic of both games; encapsulating classes like the Cell2048 and Score classes, used to save different related values in one object; a DataBaseAssistant to interact with the SQLiteDatabase, a Timer class with an interface to ease its use and an Animator2048 class to create the 2048 movements and join animations.

The database consists of two tables, the user table and the score table, where we store the most important information about the users and their scores. The relational model of the data base is as follows:

![GameCenter](https://user-images.githubusercontent.com/74202163/156825859-a65e668d-a377-4199-9430-a606b9b5fadd.png)

The front-end of the application is focused on being simple, user friendly and nice to look at. The colours of the applications were chosen with the help of the adobe palette colour generator, an adobe web page. The sounds of the application are simple, and in the games, there are sound on the fling to move the 2048 cells and on the drag and drop of the peg solitaire ball. Photos of the two games activities:

![image](https://user-images.githubusercontent.com/74202163/156851199-4c38dad7-2021-43d2-b84f-fbbd5ab85628.png) ![image](https://user-images.githubusercontent.com/74202163/156851211-0c4869b3-8663-449f-961f-059c1b00eded.png)

There are a lot of things to pay attention in this application, some of the most important are:
-	The Animator2048 class, a class that animates the movements and joins of the 2048 game, regardless of the mode of the 2048 (3x3, 4x4, 5x5, …). This is because when you instantiate this class, you can pass by parameter information about the size of the matrix and with this information the class can calculate the distance that cells have to move to arrive to its new position.

-	The management of the photos. In the settings activity where you can choose a photo from your gallery and set it as your profile picture and in the RecordActivity, where you can see a screen shot made automatically, in the 2048 game or peg solitaire game activities, by the method screenShot(View view). In both cases the photos are stored in the data base. Here you can see the photos showed in the RecordActivity that were stored when the game ended and that score was saved in the data base:

![image](https://user-images.githubusercontent.com/74202163/156850469-49a95110-e895-4724-bd3a-b5e120ddf2a1.png) ![image](https://user-images.githubusercontent.com/74202163/156850487-4cac365f-3fc6-4abf-bab7-d6d9e50e18fa.png)

-	The DataBaseAssistant, that has all the necessary methods to interact with the data base. Add, modify, delete or select users and add, delete or select scores. The scores select has a lot of parameters to filter the search as needed.

-	The Timer class and the TimerInterface, the timer interface is a classic and simple chronometer but with the interface you win a lot of efficiency. You don’t need another thread to update the view you want to update, or to do the stuff you want to do with the chronometer time value, you simply implement the interface and its method in a class and every time the chronometer is updated, the method implemented by the TimerInterface is called and all the stuff you implemented inside the method is done. 

-	The Table2048 and the TablePegSolitaire, where there is all the logic of the two games. It’s not hard to do, the harder part was the 2048 movement algorithm, but I think the methods are well separated, are clear and easy to understand and work for any version of the games you want to play. In both games you just need to change the matrix passed by parameter, but in the peg solitaire you have to implement a new method to initialize the matrix with the correct values. 

-	The use of all the things we learned this year in android. I tried to use all the things I learned as much as possible and with coherence. In this application I’m using the SQLiteDatabase (creating and dropping tables and doing selects, updates, inserts and deletes), the RecycleView with CardViews in each row (using the ViewHolder to do different things), Animators (to do the animations of the 2048 game and the SplashActivity), FragmentDialogs, Textview, Buttons, ImageViews, EditText, menu bars, different layouts, …

-	Coding best practices. Trying to do the application using the best practices possible, respecting the Object-oriented programming. Not doing super-long methods, separating the different methods correctly, and doing the same with the classes. Commenting all the methods to help understanding the code. Trying to do the cleanest code possible. Separating the related classes in different packages. Making the classes as separated as possible of the others, allowing to reuse different classes in different cases (Timer used in both games, Animator2048 used in any mode of the game 2048), and separating the back-end from the front-end as much as possible allowing to use the same class in different layouts (the 2048 classes and the peg solitaire classes are used in 3 different layouts each one).

# AUTOR:

Jaume Fullana Piza
