package org.pocketlaw.divorce_act;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gcgol on 01/09/2017.
 */

public class XmlParser {

    private Context mContext;
    private DbHelper dbHelper = DbHelper.getInstance(this.mContext);

    // We don't use namespaces
    private static final String ns = null;

    private List sections;


    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readStatute(parser);
        } finally {
            in.close();
        }
    }


    // for getting text ignoring tags
    public static String getInnerXml(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        StringBuilder sb = new StringBuilder();
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    if (depth > 0) {

                   //     sb.append("</" + parser.getName() + ">");

                        Log.e("EEEEP", "depth > 0");
                    }
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    StringBuilder attrs = new StringBuilder();
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        attrs.append(parser.getAttributeName(i) + "=\""
                                + parser.getAttributeValue(i) + "\" ");
                    }

                    Log.e("EEEEP", "depth > 0");

                    //sb.append("<" + parser.getName() + " " + attrs.toString() + ">");

                    break;
                default:
                    sb.append(parser.getText());
                    break;
            }
        }
        String content = sb.toString();
        return content;
    }


    // For skipping.
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    // Start reading
    private List readStatute(XmlPullParser parser) throws XmlPullParserException, IOException {

        sections = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "Statute");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the Body tag
            if (name.equals("Body")) {
                readBody(parser);
            } else {
                skip(parser);
            }
        }

        return sections;
    }

    // Parses the contents of the body for Heading and Section
    private void readBody(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Body");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Section")) {

                // Get the section number from the Section Code
                String section = "";
                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");
                    section = code[1];

                }

                readSection(parser, section);

            } else if (parser.getName().equals("Heading")) {

                // Get the section number from the Heading Code
                String section = "";
                String pinpoint = "";
                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");

                    // If a longer code, this generally corresponds to heading level 2,
                    // which to pinpoint require code[3]
                    if (code.length > 6) {
                        String[] split_code = code[5].split("_");
                        if (split_code.length > 0) {
                            section = split_code[1];
                        } else {
                            section = split_code[0];
                        }
                    } else  if (code.length > 4) {
                        String[] split_code = code[3].split("_");
                        if (split_code.length > 0) {
                            section = split_code[1];
                        } else {
                            section = split_code[0];
                        }
                    } else {
                        String[] split_code = code[1].split("_");
                        if (split_code.length > 0) {
                            if(split_code[0].equals("l")) {
                                section = "Part " + split_code[1];
                            } else {
                                section = split_code[1];
                            }
                        } else {
                            section = split_code[0];
                        }
                    }
                }
                if ((parser.getAttributeValue(null, "level")) != null) {
                    String[] level = parser.getAttributeValue(null, "level").split("\"");
                    pinpoint = "level" + level[0];

                    Log.e("XML", "subsectionTrue : " + pinpoint);
                }


                readHeading(parser, section, pinpoint);

            } else {
                skip(parser);
            }

        }

    }

    // Parses the contents of a Heading
    private void readHeading(XmlPullParser parser, String section, String pinpoint) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Heading");


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("TitleText")) {

                readTitleText(parser, section, pinpoint);

            } else {
                skip(parser);
            }
        }
    }

    // Parses the contents of a Section.
    private void readSection(XmlPullParser parser, String section) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Section");

        String pinpoint = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("MarginalNote")) {

                readMarginalText(parser, section);

            } else if (parser.getName().equals("Text")) {

                readSectionText(parser, section);

            } else if (parser.getName().equals("HistoricalNote")) {

                readHistoricalNote(parser, section);

            } else if (parser.getName().equals("Definition")) {

                readDefinition(parser, section, pinpoint);


            } else if (parser.getName().equals("Subsection")) {

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");
                    pinpoint = "(" + code[3] + ")";

                }

                readSubsection(parser, section, pinpoint);

            } else if (parser.getName().equals("Paragraph")) {

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");
                    pinpoint = "(" + code[3] + ")";
                }

                readParagraph(parser, section, pinpoint);


            } else {

                skip(parser);
            }

        }

    }

    // Parses the contents of a Subsection.
    private void readSubsection(XmlPullParser parser, String section, String pinpoint) throws IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            Log.e("readSubsection(", section + pinpoint);

            if (parser.getName().equals("Text")) {

                readSubsectionText(parser, section, pinpoint);

            } else if (parser.getName().equals("MarginalNote")) {

                readSubMarginalText(parser, section, pinpoint);

            } else if (parser.getName().equals("Definition")) {

                readDefinition(parser, section, pinpoint);

            } else if (parser.getName().equals("Paragraph")) {

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");
                    pinpoint = "(" + code[(code.length - 1)] + ")";
                }

                readSubsectionParagraph(parser, section, pinpoint);

            } else if (parser.getName().equals("ContinuedSectionSubsection")) {

                readContinuedSubsection(parser, section, pinpoint);

            } else {
                skip(parser);
            }
        }
    }

    // Parses the contents of a Subsection.
    private void readContinuedSubsection(XmlPullParser parser, String section, String pinpoint) throws IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readContinuedSubsectionText(parser, section, pinpoint);

            } else {
                skip(parser);
            }
        }
    }

    // Parses the contents of a Definition.
    private void readDefinition(XmlPullParser parser, String section, String pinpoint) throws IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("MarginalNote")) {

                readDefinitionMarginalNoteText(parser, section, pinpoint);

            } else if (parser.getName().equals("Text")) {

                readDefinitionText(parser, section, pinpoint);

                //Recursively call to use the readDefinitionText since the formatting is the same
            } else if (parser.getName().equals("ContinuedDefinition")) {

                readDefinition(parser, section, pinpoint);

            } else if (parser.getName().equals("Paragraph")) {

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");

                    pinpoint = "(" + code[(code.length - 1)] + ")";

                }

                readDefinitionParagraph(parser, section, pinpoint);

            } else {
                skip(parser);
            }
        }
    }

    // Parses the contents of a Paragraph.
    private void readParagraph(XmlPullParser parser, String section, String pinpoint) throws IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");

                    pinpoint = "(" + code[3] + ")";
                }

                readParagraphText(parser, section, pinpoint);

            } else if (parser.getName().equals("Subparagraph")) {

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");

                    pinpoint = "(" + code[(code.length - 1)] + ")";

                }

                readSubparagraph(parser, section, pinpoint);

            } else if (parser.getName().equals("ContinuedParagraph")) {

                readContinuedParagraph(parser, section, pinpoint);

            } else {
                skip(parser);
            }
        }
    }

    // Parses the contents of a Definition Paragraph.
    private void readDefinitionParagraph(XmlPullParser parser, String section, String pinpoint) throws IOException, XmlPullParserException {

        Log.e("XML", "readParagraph, parser.getText: " + parser.getText() + " .getName: " + parser.getName());

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readParagraphText(parser, section, pinpoint);

            } else if (parser.getName().equals("Subparagraph")) {

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");

                    pinpoint = "(" + code[(code.length - 1)] + ")";
                }

                readSubparagraph(parser, section, pinpoint);

            } else if (parser.getName().equals("ContinuedParagraph")) {

                readContinuedParagraph(parser, section, pinpoint);

            } else {
                skip(parser);
            }
        }
    }

    // Parses the contents of a Subsection Paragraph.
    private void readSubsectionParagraph(XmlPullParser parser, String section, String pinpoint) throws IOException, XmlPullParserException {

        Log.e("XML", "readSubsectionParagraph, parser.getText: " + parser.getText() + " .getName: " + parser.getName());

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                Log.e("XML", "Subsection Paragraph Text" + parser.getName());

                readSubsectionParagraphText(parser, section, pinpoint);

            } else if (parser.getName().equals("Subparagraph")) {

                Log.e("XML", "Subsection Subaragraph");

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");
                    pinpoint = "(" + code[7] + ")";

                }

                readSubsectionSubParagraph(parser, section, pinpoint);

            } else if (parser.getName().equals("ContinuedParagraph")) {

                Log.e("XML", "Continued Subsection Paragraph");

                readContinuedSubsectionParagraph(parser, section, pinpoint);

            } else {
                skip(parser);
            }
        }
    }

    // Parses the contents of a Subparagraph.
    private void readSubparagraph(XmlPullParser parser, String section, String pinpoint) throws IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readSubParagraphText(parser, section, pinpoint);


            } else {
                skip(parser);
            }
        }
    }

    // Parses the contents of a Subsection Subparagraph.
    private void readSubsectionSubParagraph(XmlPullParser parser, String section, String pinpoint) throws IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readSubsectionSubParagraphText(parser, section, pinpoint);

            } else {
                skip(parser);
            }
        }
    }

    // For Subsection Continued Subsection Paragraph values
    private void readContinuedSubsectionParagraph(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readContinuedSubsectionParagraphText(parser, section, pinpoint);

            }
        }
    }

    // For the section HistoricalNote value.
    private void readHistoricalNote(XmlPullParser parser, String section) throws
            IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("ul")) {

                readHistoryListItem(parser, section);

            } else {
                skip(parser);
            }
        }
    }

    // For History List Item
    private void readHistoryListItem(XmlPullParser parser, String section) throws IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("li")) {

                readHistoryListItemText(parser, section);

            } else {

                skip(parser);

            }
        }
    }

    // For Continued Paragraph
    private void readContinuedParagraph(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readContinuedParagraphText(parser, section, pinpoint);

            }
        }
    }


    /////////////////////////////////////
    //
    //   READ TEXT ENDPOINTS BELOW
    //
    /////////////////////////////////////

    // For the section MarginalNote value.
    private void readMarginalText(XmlPullParser parser, String section) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();


        Section resultObject = new Section(1, "marginalNote", section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readMarginalText (  1  , " + "marginalNote" + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For Subsection Continued Subsection text values.
    private void readContinuedSubsectionText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(14, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readContinuedSubsectionText (  14  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For the subsection MarginalNote value.
    private void readSubMarginalText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();


        Section resultObject = new Section(5, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readSubMarginalText (  5  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For Subsection text values.
    private void readSubsectionText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(3, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readSubsectionText (  3  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }


    // For Paragraph text values.
    private void readParagraphText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();


        Section resultObject = new Section(4, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readParagraphText (  4  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For Continued Paragraph text values.
    private void readContinuedParagraphText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(12, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readContinuedParagraphText (  12  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For Continued Subsection Paragraph text values.
    private void readContinuedSubsectionParagraphText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(13, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add Continued Subsection Paragraph Text (  13  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For SubParagraph text values.
    private void readSubParagraphText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(7, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add SubParagraph Text (  7  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For Subsection SubParagraph text values.
    private void readSubsectionSubParagraphText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(8, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add Subsection SubParagraph Text (  8  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }

    }

    // For Subsection Paragraph text values.
    private void readSubsectionParagraphText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(6, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add Subsection Paragraph Text (  6  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For History List Item Text
    private List readHistoryListItemText(XmlPullParser parser, String section) throws IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        if (text != null) {
            // if the history item starts with a space, remove it
            text = text.startsWith(" ") ? text.substring(1) : text;

            Section resultObject = new Section(9, "nopinpoint", section, text);
            dbHelper.insertSectionDetail(resultObject);

            Log.e("XML", "db add Historical Note (  9  , " + "nopinpoint" + section + " " + text + " )");
        }

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }

        return sections;
    }

    // For Section text values.
    private void readSectionText(XmlPullParser parser, String section) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        //String text = getInnerXml(parser);

        Section resultObject = new Section(2, "section text", section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add Section Text (  2  , " + "section text" + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }


    }

    // For the Definition MarginalNote value.
    private void readDefinitionMarginalNoteText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Log.e("XML", "pre add Definition Marginal Note (  10  , " + pinpoint + " " + section + " " + text + " )");

        // Skip tags within the marginal note, grab only text
        // TODO: consider a diff approach
        if (text != null) {

            Section resultObject = new Section(10, pinpoint, section, text);
            dbHelper.insertSectionDetail(resultObject);

            Log.e("XML", "db add Definition Marginal Note (  10  , " + pinpoint + section + " " + text + " )");

            if (parser.next() == XmlPullParser.START_TAG) {
                skip(parser);
            }
        }

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }

    }

    // For definition text values.
    private void readDefinitionText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Log.e("XML", "db add readDefinitionTextBEFORE (  11  , " + pinpoint + section + " " + text + " )");

        Section resultObject = new Section(11, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readDefinitionText (  11  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For the tags TitleText and level values.
    private void readTitleText(XmlPullParser parser, String section, String pinpoint) throws IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();


        Section resultObject = new Section(0, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add TitleText (  0  , " + pinpoint + "," + section + " " + text + " )");


        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }


    }

}