package com.mta.playtube.extractor.kiosk;

/*
 * Created by Christian Schabesberger on 12.08.17.
 *
 * Copyright (C) Christian Schabesberger 2017 <chris.schabesberger@mailbox.org>
 * KioskExtractor.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.mta.playtube.extractor.InfoItem;
import com.mta.playtube.extractor.ListExtractor;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.utils.Localization;

import javax.annotation.Nonnull;

public abstract class KioskExtractor<T extends InfoItem> extends ListExtractor<T> {
    private final String id;

    public KioskExtractor(StreamingService streamingService, ListLinkHandler linkHandler, String kioskId, Localization localization) {
        super(streamingService, linkHandler, localization);
        this.id = kioskId;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    /**
     * Id should be the name of the kiosk, tho Id is used for identifing it in the frontend,
     * so id should be kept in english.
     * In order to get the name of the kiosk in the desired language we have to
     * crawl if from the website.
     * @return the tranlsated version of id
     * @throws ParsingException
     */
    @Nonnull
    @Override
    public abstract String getName() throws ParsingException;
}
