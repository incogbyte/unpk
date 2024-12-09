UNPK - Android APK Extractor

UNPK is an Android application ( APK ) designed to extract and export APK files from installed applications on an Android device. It supports both base APKs and split APKs, allowing you to retrieve the necessary files for testing, backup (maybe (FAQ)).

Features
	1.	List Installed Applications:
	•	The app displays all installed applications on the device, with options to filter out system apps.
	•	Displays app name, package name, icon, and APK information (e.g., split APK support).
	2.	Search Functionality:
	•	Quickly search and filter the app list by name using a responsive search bar.
	3.	APK Export:
	•	Export the APK files (base and split) of selected applications to a user-defined directory.
	•	Uses Android’s ACTION_OPEN_DOCUMENT_TREE to safely select export destinations.
	4.	Split APK Handling:
	•	Automatically detects and handles apps with split APKs.
	•	Merges split APK components into a single exportable package structure.

How to Use
	1.	Install the application on your Android device.
	2.	Launch the app and browse the list of installed applications.
	3.	Use the search bar to filter apps by name.
	4.	Select one or more apps and tap the Export button.
	5.	Choose a directory to save the APK files.
	6.	View success or error notifications after the export.

Code Structure
	•	MainActivity:
	•	Handles the UI, search bar, app list, and export actions.
	•	AppListAdapter:
	•	Adapter for displaying app details in the ListView.
	•	APKExporter:
	•	Core logic for extracting and exporting APK files.
	•	PackageHelper:
	•	Utility for fetching installed apps and managing APK paths.

Screenshots

![](/images/SCR-20241209-oezg.png)

License

This project is licensed under the MIT License.
