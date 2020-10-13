package main.java;

import com.voicerss.tts.*;
import main.java.model.Dictionary;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import java.util.Map;

public class SearchEngineController extends Dictionary {
    
    private static final String HISTORY_FILE_PATH = "src/main/resources/history.txt";
    ObservableList<String> relatedWords = FXCollections.observableArrayList();

    @FXML
    private Label warningMessageLabel;
    @FXML
    private FontAwesomeIconView warningIcon;
    @FXML
    private FontAwesomeIconView pronunciationIcon;
    @FXML
    private TextField wordToSearchField;
    @FXML
    private TextArea wordDefinitionArea;
    @FXML
    private TextArea wordHistoryArea;
    @FXML
    private ListView<String> relatedWordList;

    public void searchForWord() {
        getDefinition();
        showRelatedWordList();
    }

    public void getDefinition() {
        if (dictionary.containsKey(wordToSearchField.getText())) {
            wordDefinitionArea.setText(wordToSearchField.getText() + "\n\n" + dictionary.get(wordToSearchField.getText()));
            pronunciationIcon.setVisible(true);
            if (!searchedWords.contains(wordToSearchField.getText())){
                addToHistory();
            }
        }
    }

    public void showRelatedWordList() {
        String pattern = wordToSearchField.getText();
        if (!pattern.equals("")) {
            relatedWords.clear();
            boolean isExisted = false;
            for (Map.Entry<String, String> word : dictionary.entrySet()) {
                if (word.getKey().startsWith(pattern)) {
                    relatedWords.add(word.getKey());
                    isExisted = true;
                }
            }
            warningIcon.setVisible(!isExisted);
            warningMessageLabel.setVisible(!isExisted);
            relatedWordList.getItems().clear();
            relatedWordList.getItems().addAll(relatedWords);
            relatedWordList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
    }

    public void getDefinitionOfSelectedWord() {
        String selectedWord = relatedWordList.getSelectionModel().getSelectedItem();
        wordDefinitionArea.setText(dictionary.get(selectedWord));
        pronunciationIcon.setVisible(true);
    }

    public void textToSpeech() throws Exception {
        String selectedWord = relatedWordList.getSelectionModel().getSelectedItem();
        VoiceProvider tts = new VoiceProvider("cc4d6af20685439b9b77544383b558fc");

        VoiceParameters params = new VoiceParameters(selectedWord, Languages.English_UnitedStates);
        params.setCodec(AudioCodec.WAV);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);

        byte[] voice = tts.speech(params);

        FileOutputStream fos = new FileOutputStream("src/main/resources/voice.mp3");
        fos.write(voice, 0, voice.length);
        fos.flush();
        fos.close();
    }

    public void addToHistory() {
        try {
            searchedWords.add(wordToSearchField.getText());
            FileWriter fileWriter = new FileWriter(HISTORY_FILE_PATH, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(wordToSearchField.getText()+"\n");
            bufferedWriter.close();
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    public void showHistorySearch() {
        StringBuilder result = new StringBuilder();
        for (String word : searchedWords) {
            result.append(word).append("\n");
        }
        wordHistoryArea.setText(result.toString());
    }

    public void addNewWord() {
        NewWordBoxController.openNewWordBox();
    }

    public void editWord() {
        if (!wordToSearchField.getText().equals("")){
            EditBoxController.openEditBox(wordToSearchField.getText());
        }
    }

    public void deleteWord() {
        DeleteWordController.openDeleteWordWindow();
    }

    public void aboutUs() {
        AboutUsController.openAboutUsWindow();
    }
}