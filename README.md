eDOTS
=====
1. Calendar - @Nishant
	1. Clean up code base and comment
 	2. Tabs to go between medical history and calendar
	3. Save calendar locally (should we even do this?)
	4. Schedule Table in SQL (not us)

2. Get Patient (Brendan)
  1. If they enter a Valid DNI, ask if they want to add that patient as one of their promoters in the drop down, and add that to the table (need to reload patient list for dropdown)
	2. If patient already listed don't allow to add patient
  3. If in drop_down, the document_type indicates that they should have an ID and they don't then it should prompt them to enter a valid one (fairly non-essential, not really a promoter use case)
	


3. Register Patients
  1. add phone numbers and communicate with Pacientes_contactos
  2. If internet cuts out, then add some banner that alerts them @JN and @Ankit (with what is possible) - DONE
  3. Add to usuarios_pacients on registration @Brendan
  4. change list of active treatment from dummy values to real values

4. New Visit
  1. Make sure visit group, visit_id, and projects are real numbers (right now they are dummy values) (@Lili) - DONE
  2. Making logging a new visit uninternetable
  3. fix enrolled project name @lili
 
5. SMS - @JN and @Ankit (with what is possible)
  1. Knowing which patients to send texts to
  2. What should the content of the text be
	3. Being able to find relationship between promoters and coordinators

6. General
  1. Refactor LoadTasks so that offline and online happen in the same class


Overview of App Structure:

java.edots.models: Contains the classes for the various data models that are used throughout the app
java.edots.tasks: Contains the classes for server communication
java.edots.utils: Contains cross-activity utility functions, such as those related to internet connection, sending SMS, or dealing with time forms.

java.org.techintheworld.www.edots: Contains the controller activities for each of the views.
