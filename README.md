eDOTS
=====
1. Calendar - Nishant
  1. Showing Red and Green based on Visits
  2. Making individual days clickable
  3. Implementing some weekly/broader view
  4. Schedule Table in SQL (not us)

2. Get Patient (Brendan)
  1. If they enter invalid DNI, there should be a dialogue that tells them this patient is invalid and asks if they want to register this Patient
  2. If they enter a Valid DNI, ask if they want to add that patient as one of their promoters in the drop down, and add that to the table
  3. If in drop_down, the document_type indicates that they should have an ID and they don't then it should prompt them to enter a valid one'
  4. Adding to usuarios_pacientes generally


3. Register Patients
  1. add phone numbers and communicate with Pacientes_contactos
  2. If internet cuts out, then add some banner that alerts them JN and Ankit (with what is possible) - DONE


4. New Visit
  1. Make sure visit group, visit_id, and projects are real numbers (right now they are dummy values) 
 
5. SMS - JN and Ankit (with what is possible)
  1. Knowing which patients to send texts too
  2. What should the content of the text be
	3. Being able to find relationship between promoters and coordinators



Overview of App Structure:

java.edots.models: Contains the classes for the various data models that are used throughout the app
java.edots.tasks: Contains the classes for server communication
java.edots.utils: Contains cross-activity utility functions, such as those related to internet connection, sending SMS, or dealing with time forms.

java.org.techintheworld.www.edots: Contains the controller activities for each of the views.
