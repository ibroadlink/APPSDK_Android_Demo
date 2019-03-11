package cn.com.broadlink.blappsdkdemo.utils.http;


import org.apache.http.entity.mime.MultipartEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpMultipartEntity extends MultipartEntity {
	private final ProgressListener mProgressListener;

	public HttpMultipartEntity(final ProgressListener listener) {
		super();
		this.mProgressListener = listener;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.mProgressListener));
	}

	public static interface ProgressListener {
		void transferred(long num);
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener progressListener;
		private long transferred;

		public CountingOutputStream(final OutputStream out, final ProgressListener listener) {
			super(out);
			this.progressListener = listener;
			this.transferred = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;

			if (this.progressListener != null)
				this.progressListener.transferred(this.transferred);
		}

		public void write(int b) throws IOException {

			out.write(b);

			this.transferred++;

			if (this.progressListener != null)
				this.progressListener.transferred(this.transferred);
		}
	}
}