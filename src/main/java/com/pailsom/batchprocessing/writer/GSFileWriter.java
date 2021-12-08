package com.pailsom.batchprocessing.writer;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.WriteFailedException;
import org.springframework.batch.item.file.FlatFileItemWriter;


import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.util.List;

public class GSFileWriter<T> extends FlatFileItemWriter<T> {

    private Storage storage;

    private String bucketName;

    private String fileName;

    private static final String DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");
    private Writer os;

    private String lineSeparator = DEFAULT_LINE_SEPARATOR;

    @Override
    public void write(List<? extends T> items) throws Exception {

        StringBuilder lines = new StringBuilder();
        for (T item : items) {
            lines.append(item).append(lineSeparator);
        }
        byte[] bytes = lines.toString().getBytes();
        try {
            for (T item : items) {
                os.write(item.toString());
                os.write(lineSeparator);
            }
        }
        catch (IOException e) {
            throw new WriteFailedException("Could not write data.  The file may be corrupt.", e);
        }

        os.flush();
    }

    @Override
    public void open(ExecutionContext executionContext) {

        BlobInfo info = BlobInfo.newBuilder(bucketName, fileName).build();
        final Blob blob = storage.create(info);
        final WriteChannel writer = blob.writer(Storage.BlobWriteOption.generationMatch());
        os = Channels.newWriter(writer, Charset.defaultCharset().name());
    }

    @Override
    public void update(ExecutionContext executionContext) {
    }

    @Override
    public void close() {
        super.close();

        try {
            os.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    @Override
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }
}
