eDOTS
1. Calendar - @Nishant
	1. Clean up code base and comment
 	2. Tabs to go between medical history and calendar
	3. Save calendar locally (should we even do this?)
	4. Schedule Table in SQL (not us)

2. Get Patient (Brendan)
  1. If in drop_down, the document_type indicates that they should have an ID and they don't then it should prompt them to enter a valid one (fairly non-essential, not really a promoter use case)
	

3. Register Patients
  1. add phone numbers and communicate with Pacientes_contactos
  2. If internet cuts out, then add some banner that alerts them @JN and @Ankit (with what is possible) - DONE
  3. Add to usuarios_pacients on registration @Brendan
  4. change list of active treatment from dummy values to real values

4. New Visit
  1. Make sure visit group, visit_id, and projects are real numbers (right now they are dummy values) (@Lili) - DONE
  2. Making logging a new visit uninternetable
  3. fix enrolled project name - DONE
 
5. SMS - @JN and @Ankit (with what is possible)
  1. Knowing which patients to send texts to
  2. What should the content of the text be
	3. Being able to find relationship between promoters and coordinators

<<<<<<< HEAD
6. Offline Storage refactoring and saving visits - JN
    1. save project the patients are enrolled
=======
6. General
  1. Refactor LoadTasks so that offline and online happen in the same class (working on it for visits)
  2. do not hardcode in server name for all load tasks

7. Promoter Login
  1. sometimes need to press log in button twice to log in

Things to Communicate with Juan
1. General layout of code, which activities lead to which, what packages mean
2. SMS 
3. Shared Preferences
4. Offline functionality
5. Fingerprint Reader 

>>>>>>> aa98cd360799fe3d3e4233d4871ea8d3423c17d3


Overview of App Structure:

java.edots.models: Contains the classes for the various data models that are used throughout the app
java.edots.tasks: Contains the classes for server communication
java.edots.utils: Contains cross-activity utility functions, such as those related to internet connection, sending SMS, or dealing with time forms.

java.org.techintheworld.www.edots: Contains the controller activities for each of the views.
