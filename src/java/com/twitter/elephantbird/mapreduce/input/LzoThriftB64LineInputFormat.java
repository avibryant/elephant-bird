package com.twitter.elephantbird.mapreduce.input;

import java.io.IOException;

import com.twitter.elephantbird.mapreduce.io.ThriftWritable;
import com.twitter.elephantbird.util.ThriftUtils;
import com.twitter.elephantbird.util.TypeRef;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.thrift.TBase;

/**
 * Reads line from an lzo compressed text file, base64 decodes it, and then
 * deserializes that into the Thrift object.
 * Returns <position, thriftObject> pairs. <br><br>
 *
 * Do not use LzoThriftB64LineInputFormat.class directly for setting
 * InputFormat class for a job. Use getInputFormatClass() instead.
 */
public class LzoThriftB64LineInputFormat<M extends TBase<?, ?>>
                extends LzoInputFormat<LongWritable, ThriftWritable<M>> {

  public LzoThriftB64LineInputFormat() {}

  /**
   * Returns {@link LzoThriftB64LineInputFormat} class for setting up a job.
   * Sets an internal configuration in jobConf so that Task instantiates
   * appropriate object for this generic class based on thriftClass
   */
  @SuppressWarnings("unchecked")
  public static <M extends TBase<?, ?>> Class<LzoThriftB64LineInputFormat>
     getInputFormatClass(Class<M> thriftClass, Configuration jobConf) {
    ThriftUtils.setClassConf(jobConf, LzoThriftB64LineInputFormat.class, thriftClass);
    return LzoThriftB64LineInputFormat.class;
  }

  @Override
  public RecordReader<LongWritable, ThriftWritable<M>> createRecordReader(InputSplit split,
      TaskAttemptContext taskAttempt) throws IOException, InterruptedException {

    TypeRef<M> typeRef = ThriftUtils.getTypeRef(taskAttempt.getConfiguration(), LzoThriftB64LineInputFormat.class);
    return new LzoThriftB64LineRecordReader<M>(typeRef);
  }

}
