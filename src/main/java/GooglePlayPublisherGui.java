import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.LocalizedText;
import com.google.api.services.androidpublisher.model.Track;
import com.google.api.services.androidpublisher.model.TrackRelease;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Dimension;
import java.awt.Label;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Objects;

public class GooglePlayPublisherGui extends JFrame {

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final Dimension TEXTFIELD_SIZE = new Dimension(120, 25);
	private static final String MIME_TYPE_APK = "application/vnd.android.package-archive";
	private static final String MIME_TYPE_APP_BUNDLE = "application/octet-stream";
	private static HttpTransport HTTP_TRANSPORT;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	private File serviceAccountKeyFile;

	public GooglePlayPublisherGui() {
		super("Google Play Publisher");

		var mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		var appNamePanel = new JPanel();
		var appNameTextBox = new JTextField();
		appNameTextBox.setPreferredSize(TEXTFIELD_SIZE);
		appNamePanel.add(new JLabel("App name: "));
		appNamePanel.add(appNameTextBox);

		var packageNamePanel = new JPanel();
		var packageNameTextBox = new JTextField();
		packageNameTextBox.setPreferredSize(TEXTFIELD_SIZE);
		packageNamePanel.add(new JLabel("Package name: "));
		packageNamePanel.add(packageNameTextBox);

		var trackPanel = new JPanel();
		var radioButtonGroup = new JRadioButtonGroup();
		var alphaTrack = new JRadioButton("Alpha", true);
		alphaTrack.setName(Tracks.ALPHA.getTrackName());
		var betaTrack = new JRadioButton("Beta");
		betaTrack.setName(Tracks.BETA.getTrackName());
		var prodTrack = new JRadioButton("Production");
		prodTrack.setName(Tracks.PRODUCTION.getTrackName());
		trackPanel.add(new JLabel("Choose track: "));
		radioButtonGroup.add(alphaTrack, betaTrack, prodTrack);
		trackPanel.add(radioButtonGroup);

		var trackStatusPanel = new JPanel();
		var trackStatusRadioButtonGroup = new JRadioButtonGroup();
		var completedStatus = new JRadioButton("Completed", true);
		completedStatus.setName(TrackStatus.COMPLETED.getStatusName());
		var draftStatus = new JRadioButton("Draft");
		draftStatus.setName(TrackStatus.DRAFT.getStatusName());
		var haltedStatus = new JRadioButton("Halted");
		haltedStatus.setName(TrackStatus.HALTED.getStatusName());
		var inProgressStatus = new JRadioButton("In Progress");
		inProgressStatus.setName(TrackStatus.IN_PROGRESS.getStatusName());
		trackStatusPanel.add(new JLabel("Choose release status: "));
		trackStatusRadioButtonGroup.add(completedStatus, draftStatus, haltedStatus, inProgressStatus);
		trackStatusPanel.add(trackStatusRadioButtonGroup);

		var chooseKeyPanel = new JPanel();
		var chooseKeyButton = new JButton("Choose...");
		chooseKeyButton.addActionListener(e -> chooseKeyFile());
		chooseKeyPanel.add(new Label("Choose service account key (JSON): "));
		chooseKeyPanel.add(chooseKeyButton);

		var buttonPanel = new JPanel();
		var uploadApkButton = new JButton("Upload APK");
		var uploadAppBundleButton = new JButton("Upload App Bundle");
		buttonPanel.add(uploadApkButton);
		buttonPanel.add(uploadAppBundleButton);

		mainPanel.add(appNamePanel);
		mainPanel.add(packageNamePanel);
		mainPanel.add(trackPanel);
		mainPanel.add(trackStatusPanel);
		mainPanel.add(chooseKeyPanel);
		mainPanel.add(buttonPanel);

		uploadApkButton.addActionListener(e -> upload(appNameTextBox.getText().strip(), packageNameTextBox.getText().strip(), radioButtonGroup.getSelected().getName(), trackStatusRadioButtonGroup.getSelected().getName(), AppType.APK));
		uploadAppBundleButton.addActionListener(e -> upload(appNameTextBox.getText().strip(), packageNameTextBox.getText().strip(), radioButtonGroup.getSelected().getName(), trackStatusRadioButtonGroup.getSelected().getName(), AppType.APP_BUNDLE));

		this.add(mainPanel);
	}

	private void chooseKeyFile() {
		var fileChooser = new JFileChooser();
		fileChooser.setApproveButtonText("Choose!");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
		var result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			this.serviceAccountKeyFile = fileChooser.getSelectedFile();
		}
	}

	private AndroidPublisher createService(String appName) {
		if (this.serviceAccountKeyFile == null) {
			return null;
		}

		GoogleCredentials credentials;
		try {
			credentials = GoogleCredentials.fromStream(new FileInputStream(this.serviceAccountKeyFile));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Could not create service!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();

			return null;
		}

		credentials = credentials.createScoped(Collections.singleton("https://www.googleapis.com/auth/androidpublisher"));
		// Set up and return API client.
		return new AndroidPublisher.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials)).setApplicationName(appName)
				.build();
	}

	private void upload(String appName, String packageName, String trackName, String trackStatus, AppType appType) {
		if (this.serviceAccountKeyFile == null) {
			JOptionPane.showMessageDialog(null, "Please select the key file for your service account", "Missing key file", JOptionPane.ERROR_MESSAGE);

			return;
		}

		if (appName == null || appName.isBlank() || packageName == null || packageName.isBlank()) {
			JOptionPane.showMessageDialog(null, "Please specify both app and package name", "Missing info", JOptionPane.ERROR_MESSAGE);

			return;
		}

		var releaseNotes = Objects.requireNonNullElse(JOptionPane.showInputDialog(null, "Release notes:"), "");

		var fileChooser = new JFileChooser();
		fileChooser.setApproveButtonText("Upload!");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if (appType == AppType.APK) {
			fileChooser.setFileFilter(new FileNameExtensionFilter("APK files", "apk"));
		} else if (appType == AppType.APP_BUNDLE) {
			fileChooser.setFileFilter(new FileNameExtensionFilter("App bundle files", "aab"));
		}

		var result = fileChooser.showOpenDialog(null);
		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}

		var service = createService(appName);
		if (service == null) {
			return;
		}

		try {
			// Create edit
			var edit = service.edits();
			var insert = edit.insert(packageName, null);
			var editId = insert.execute().getId();
			Integer versionCode = 1;
			if (appType == AppType.APK) {
				var uploadApkRequest = edit.apks().upload(packageName, editId, new FileContent(MIME_TYPE_APK, fileChooser.getSelectedFile()));
				versionCode = uploadApkRequest.execute().getVersionCode();
				System.out.printf("Version code %d has been uploaded.\n", versionCode);
			} else if (appType == AppType.APP_BUNDLE) {
				var uploadAppBundleRequest = edit.bundles().upload(packageName, editId, new FileContent(MIME_TYPE_APP_BUNDLE, fileChooser.getSelectedFile()));
				versionCode = uploadAppBundleRequest.execute().getVersionCode();
				System.out.printf("Version code %d has been uploaded.\n", versionCode);
			}

			//Set track
			var trackRelease = new TrackRelease()
					.setName(String.format("Release %d", versionCode))
					.setVersionCodes(Collections.singletonList(Long.valueOf(versionCode)))
					.setStatus(trackStatus)
					.setReleaseNotes(Collections.singletonList(new LocalizedText().setLanguage("de-DE").setText(releaseNotes)));
			var trackRequest = edit.tracks().update(packageName, editId, trackName, new Track().setReleases(Collections.singletonList(trackRelease)));
			trackRequest.execute();

			//Commit edit
			var commit = edit.commit(packageName, editId).execute();
			JOptionPane.showMessageDialog(null, String.format("App edit with id %s has been committed!\n", commit.getId()), "Success", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "App could not be uploaded! Transaction was rolled back.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private enum AppType {
		APK, APP_BUNDLE
	}

	private enum Tracks {
		ALPHA("alpha"), BETA("beta"), PRODUCTION("prod");

		private final String trackName;

		Tracks(String trackName) {
			this.trackName = trackName;
		}

		public String getTrackName() {
			return trackName;
		}
	}

	private enum TrackStatus {
		COMPLETED("completed"), DRAFT("draft"), HALTED("halted"), IN_PROGRESS("inProgress");

		private final String statusName;

		TrackStatus(String statusName) {
			this.statusName = statusName;
		}

		public String getStatusName() {
			return statusName;
		}
	}
}
