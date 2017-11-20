package pl.edu.prz.weii.ChinesePostmanProblem.domain.file;

import com.vaadin.server.StreamVariable;
import com.vaadin.ui.Notification;

import java.io.*;
import java.util.stream.Collectors;

public abstract class FileToFileContent implements StreamVariable {

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public abstract void process(FileContent fileContent);

    @Override
    public void streamingFinished(StreamingEndEvent event) {
        Notification.show("Uploaded: " + event.getFileName());
        try (BufferedReader reader = new BufferedReader(new StringReader(new String(outputStream.toByteArray())))) {
            process(new FileContent(reader.lines().collect(Collectors.toList())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public boolean listenProgress() {
        return true;
    }

    @Override
    public void onProgress(StreamingProgressEvent event) {
    }

    @Override
    public void streamingStarted(StreamingStartEvent event) {
    }

    @Override
    public void streamingFailed(StreamVariable.StreamingErrorEvent event) {
        Notification.show("Upload failed for file: " + event.getFileName());
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }
}
