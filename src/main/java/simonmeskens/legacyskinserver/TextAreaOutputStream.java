package simonmeskens.legacyskinserver;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TextAreaOutputStream extends OutputStream {

    private final JTextArea textArea;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public TextAreaOutputStream(final JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public void write(int b) throws IOException {
        if (b == '\r')
            return;

        if (b == '\n') {
            final String text = new String(buffer.toByteArray(), StandardCharsets.UTF_8) + "\n";
            buffer.reset();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.append(text);
                }
            });
            return;
        }

        buffer.write(b);
    }
}