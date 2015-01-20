eDOTS
1. Calendar - @Nishant wrapping up Calendar
	1. Clean up code base and comment
 	2. Tabs to go between medical history and calendar
	3. If Offline, ThenSome Alert- DECIDING AGAINST THIS
	4. Legend For Calendar
	5. Changing Schedule from real schedule

2. Get Patient
	1. If no schedule, make them create one when they try and log a new visit (Brendan)
	2. Changing treatments for patients (Lili)
	3. Delete patient from promoter

3. Register Patients
  1. add phone numbers and communicate with Pacientes_contactos - DONE
  2. If internet cuts out, then add some banner that alerts them @JN and @Ankit (with what is possible) - DONE
  3. Add to usuarios_pacients on registration @Brendan - DONE
  4. change list of active treatment from dummy values to real values -Awaiting Web Method From Juan (Lili)

4. New Visit
  1. Make sure visit group, visit_id, and projects are real numbers (right now they are dummy values) (@Lili) - DONE
  2. Making logging a new visit uninternetable
  3. fix enrolled project name - DONE
 
5. SMS - @JN and @Ankit (with what is possible)
  1. Knowing which patients to send texts to -QUERY DONE, AWAITING WEBMETHOD, OUR SIDE DONE
  2. What should the content of the text be -DONE
	3. Being able to find relationship between promoters and coordinators - YEAH
	4. Deciding when to Send Texts
	5. Should texts be automated? It would only involve only clicking a button.
	

6. General
  1. Refactor LoadTasks so that offline and online happen in the same class (working on it for visits, saving is not done, offline upload works)
		- JN AND ANKIT are gonna kill this
  2. do not hardcode in server name for all load tasks (MOSTLY DONE)

7. Promoter Login
  1. sometimes need to press log in button twice, log in spinner spins even when password is wrong, sometimes doesn't spin, its nice though we like it


Things to Communicate with Juan
1. General layout of code, which activities lead to which, what packages mean
2. SMS 
3. Shared Preferences
4. Offline functionality
5. Fingerprint Reader 



Overview of App Structure:

java.edots.models: Contains the classes for the various data models that are used throughout the app
java.edots.tasks: Contains the classes for server communication
java.edots.utils: Contains cross-activity utility functions, such as those related to internet connection, sending SMS, or dealing with time forms.

java.org.techintheworld.www.edots: Contains the controller activities for each of the views.
