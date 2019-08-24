package com.mta.playtube.extractor.comments;

import com.mta.playtube.extractor.ListExtractor;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.utils.Localization;

public abstract class CommentsExtractor extends ListExtractor<CommentsInfoItem> {

	public CommentsExtractor(StreamingService service, ListLinkHandler uiHandler, Localization localization) {
		super(service, uiHandler, localization);
		// TODO Auto-generated constructor stub
	}

}
