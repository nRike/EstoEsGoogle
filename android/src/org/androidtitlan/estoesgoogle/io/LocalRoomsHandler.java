package org.androidtitlan.estoesgoogle.io;

import org.androidtitlan.estoesgoogle.R;

import org.androidtitlan.estoesgoogle.provider.ScheduleContract;
import org.androidtitlan.estoesgoogle.provider.ScheduleContract.Rooms;
import org.androidtitlan.estoesgoogle.util.Lists;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;

import java.io.IOException;
import java.util.ArrayList;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

/**
 * Handle a local {@link XmlPullParser} that defines a set of {@link Rooms}
 * entries. Usually loaded from {@link R.xml} resources.
 */
public class LocalRoomsHandler extends XmlHandler {

    public LocalRoomsHandler() {
        super(ScheduleContract.CONTENT_AUTHORITY);
    }

    /** {@inheritDoc} */
    @Override
    public ArrayList<ContentProviderOperation> parse(XmlPullParser parser, ContentResolver resolver)
            throws XmlPullParserException, IOException {
        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        int type;
        while ((type = parser.next()) != END_DOCUMENT) {
            if (type == START_TAG && Tags.ROOM.equals(parser.getName())) {
                parseRoom(parser, batch, resolver);
            }
        }

        return batch;
    }

    /**
     * Parse a given {@link Rooms} entry, building
     * {@link ContentProviderOperation} to define it locally.
     */
    private static void parseRoom(XmlPullParser parser,
            ArrayList<ContentProviderOperation> batch, ContentResolver resolver)
            throws XmlPullParserException, IOException {
        final int depth = parser.getDepth();
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newInsert(Rooms.CONTENT_URI);

        String tag = null;
        int type;
        while (((type = parser.next()) != END_TAG ||
                parser.getDepth() > depth) && type != END_DOCUMENT) {
            if (type == START_TAG) {
                tag = parser.getName();
            } else if (type == END_TAG) {
                tag = null;
            } else if (type == TEXT) {
                final String text = parser.getText();
                if (Tags.ID.equals(tag)) {
                    builder.withValue(Rooms.ROOM_ID, text);
                } else if (Tags.NAME.equals(tag)) {
                    builder.withValue(Rooms.ROOM_NAME, text);
                } else if (Tags.FLOOR.equals(tag)) {
                    builder.withValue(Rooms.ROOM_FLOOR, text);
                }
            }
        }

        batch.add(builder.build());
    }

    /** XML tags expected from local source. */
    private interface Tags {
        String ROOM = "room";
        String ID = "id";
        String NAME = "name";
        String FLOOR = "floor";
    }
}
