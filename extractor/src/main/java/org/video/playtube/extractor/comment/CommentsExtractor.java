package org.video.playtube.extractor.comment;

import org.video.playtube.extractor.ListExtractor;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.util.Localization;

public abstract class CommentsExtractor extends ListExtractor<CommentsInfoItem> {

	public CommentsExtractor(StreamingService service, ListLinkHandler uiHandler, Localization localization) {
		super(service, uiHandler, localization);
		// TODO Auto-generated constructor stub
	}

}
