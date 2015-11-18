package videoLoop;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.swing.VideoComponent;

public class TestApplication {
	public TestApplication() {
	}

	private static Pipeline pipe;

	public static void main(String[] args) {
		args = Gst.init("TestApplication", args);
		pipe = new Pipeline("VideoTest");
		final Element videosrc = ElementFactory.make("v4l2src", "source");
		videosrc.set("device", "/dev/video0");
		final Element videofilter = ElementFactory.make("capsfilter", "filter");
		videofilter.setCaps(Caps.fromString("video/x-raw-yuv"));
		final Element videoTee = ElementFactory.make("tee", "tee");

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				VideoComponent videoComponent = new VideoComponent();
				videoComponent.getElement().setName("video1");
				videoComponent.setPreferredSize(new Dimension(720, 576));
				VideoComponent videoComponent2 = new VideoComponent();
				videoComponent2.getElement().setName("video2");
				videoComponent2.setPreferredSize(new Dimension(720, 576));

				Element videosink = videoComponent.getElement();
				Element videosink2 = videoComponent2.getElement();
				pipe.addMany(videosrc, videofilter, videoTee, videosink, videosink2);
				Element.linkMany(videosrc, videofilter, videoTee);
				Element.linkMany(videoTee, videosink);
				Element.linkMany(videoTee, videosink2);

				JFrame projectionFrame = new JFrame("Swing Video Test");
				projectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				projectionFrame.add(videoComponent, BorderLayout.CENTER);
				projectionFrame.pack();
				projectionFrame.setVisible(true);

				JFrame monitorFrame = new JFrame("monitor");

				monitorFrame.add(videoComponent2, BorderLayout.CENTER);
				monitorFrame.pack();
				monitorFrame.setVisible(true);
				pipe.setState(State.PLAYING);
			}
		});

	}
}
