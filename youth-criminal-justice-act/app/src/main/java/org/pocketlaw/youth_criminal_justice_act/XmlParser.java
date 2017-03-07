package org.pocketlaw.youth_criminal_justice_act;

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


    // For skipping.
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        String isSkipping = parser.getName();


        // TODO: this hack seems to get around a bug i cannot understand when
        // TODO: parsing the income tax act after s56(3)(c) -- not sure of any negative impacts on
        // TODO: Income Tax Act or others....
        // TODO: TEST - but seems to work right now
        if (parser.getName().equals("Subsection")) {
            isSkipping = "SAVED";
            String section = "";
            String pinpoint = "";
            if ((parser.getAttributeValue(null, "Code")) != null) {
                String[] code = parser.getAttributeValue(null, "Code").split("\"");
                section = code[1];
                pinpoint = code[code.length - 1];
            }
            readSubsection(parser, section, pinpoint);
        }

        // TODO: for income tax act again, kinda hacky
        if (parser.getName().equals("Section")) {
            isSkipping = "SAVED";
            String section = "";
            if ((parser.getAttributeValue(null, "Code")) != null) {
                String[] code = parser.getAttributeValue(null, "Code").split("\"");
                section = code[1];
            }
            readSection(parser, section);
        }


        // TODO: for income tax act again, kinda hacky
        if (parser.getName().equals("ContinuedSubparagraph")) {
            isSkipping = "SAVED";
            String section = "";
            String pinpoint = "";
            if ((parser.getAttributeValue(null, "Code")) != null) {
                String[] code = parser.getAttributeValue(null, "Code").split("\"");
                section = code[1];
                pinpoint = code[code.length - 1];
            }
            readContinuedSubsectionSubparagraph(parser, section, pinpoint);

        } else if (parser.getName().equals("FormulaGroup")) {
            isSkipping = "SAVED";
            String section = "";
            String pinpoint = "";
            if ((parser.getAttributeValue(null, "Code")) != null) {
                String[] code = parser.getAttributeValue(null, "Code").split("\"");
                section = code[1];
                pinpoint = code[code.length - 1];
            }
            readFormulaGroup(parser, section, pinpoint);

        } else if (parser.getName().equals("FormulaDefinition")) {
            isSkipping = "SAVED";
            String section = "";
            String pinpoint = "";
            if ((parser.getAttributeValue(null, "Code")) != null) {
                String[] code = parser.getAttributeValue(null, "Code").split("\"");
                section = code[1];
                pinpoint = code[code.length - 1];
            }
            readFormulaDefinition(parser, section, pinpoint);

        } else if (parser.getName().equals("ContinuedClause")) {
            isSkipping = "SAVED";
            String section = "";
            String pinpoint = "";
            if ((parser.getAttributeValue(null, "Code")) != null) {
                String[] code = parser.getAttributeValue(null, "Code").split("\"");
                section = code[1];
                pinpoint = code[code.length - 1];
            }
            readContinuedClause(parser, section, pinpoint);

        } else if (parser.getName().equals("ContinuedSubclause")) {
            isSkipping = "SAVED";
            String section = "";
            String pinpoint = "";
            if ((parser.getAttributeValue(null, "Code")) != null) {
                String[] code = parser.getAttributeValue(null, "Code").split("\"");
                section = code[1];
                pinpoint = code[code.length - 1];
            }
            readContinuedSubclause(parser, section, pinpoint);

        } else if (parser.getName().equals("FormulaParagraph")) {
            isSkipping = "SAVED";
            String section = "";
            String pinpoint = "";
            if ((parser.getAttributeValue(null, "Code")) != null) {
                String[] code = parser.getAttributeValue(null, "Code").split("\"");
                section = code[1];
                pinpoint = code[code.length - 1];
            }
            readFormulaParagraph(parser, section, pinpoint);

        } else if (parser.getName().equals("ContinuedFormulaParagraph")) {
            isSkipping = "SAVED";
            String section = "";
            String pinpoint = "";
            if ((parser.getAttributeValue(null, "Code")) != null) {
                String[] code = parser.getAttributeValue(null, "Code").split("\"");
                section = code[1];
                pinpoint = code[code.length - 1];
            }
            readContinuedParagraph(parser, section, pinpoint);

        } else if (parser.getName().equals("Oath")) {
            isSkipping = "SAVED";
            String section = "Oath";

            readHistorySmallText(parser, section);

        } else if (parser.getName().equals("Footnote")) {
            isSkipping = "SAVED";

            readFootnote(parser);

        } else if (parser.getName().equals("Note")) {
            isSkipping = "SAVED";

            readHistorySmallText(parser, "Note");

        } else if (parser.getName().equals("Provision")) {
            isSkipping = "SAVED";
            String section = "";
            if ((parser.getAttributeValue(null, "Code")) != null) {
                String[] code = parser.getAttributeValue(null, "Code").split("\"");
                section = code[1];
            }
            readProvision(parser, section);

        } else if (parser.getName().equals("HistoricalNote")) {
            isSkipping = "SAVED";
            String section = "";
            if ((parser.getAttributeValue(null, "Code")) != null) {
                String[] code = parser.getAttributeValue(null, "Code").split("\"");
                section = code[1];
            }
            readHistoricalNote(parser, section);

        } else if (parser.getName().equals("Identification")) {
            isSkipping = "SAVED";
            readIdentification(parser);

        } else if (parser.getName().equals("ReaderNote")) {
            isSkipping = "SAVED";
            readReaderNote(parser);


        } else {

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

        //TODO: go back and make sure you can skip these when you are
        if (!((isSkipping.equals("Label") || (isSkipping.equals("MarginalNote") || (isSkipping.equals("a")) || (isSkipping.equals("SAVED")))))) {
            Log.e("EEEE", "SKIP: " + isSkipping);
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

                // TODO: Because section getting is done in readHeading, I think this if statement can be deleted
                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");

                    String[] split_code = code[code.length - 2].split("_");

                    if (split_code[0].equals("l")) {
                        section = "Part " + split_code[split_code.length - 1];
                    } else {
                        section = split_code[split_code.length - 1];
                    }

                    Log.e("EEEE", "to readHeading for section: " + section);

                }


                if ((parser.getAttributeValue(null, "level")) != null) {
                    String[] level = parser.getAttributeValue(null, "level").split("\"");
                    pinpoint = "level" + level[0]; // using pinpoint for level here as no further pinpoint necessary

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

//                String regexStr = "^[0-9]*$";
                String regexStr = "(.*)[A-Z](.*)";    //RegEx to attempt override if not already a number and not first word Part

                if ((section.trim().matches(regexStr))) {

                    Log.e("EEEE", "heading section MATCH REGEX: " + section);

                    if ((parser.getAttributeValue(null, "Code")) != null) {
                        String[] code = parser.getAttributeValue(null, "Code").split("\"");

                        String[] split_code = code[code.length - 4].split("_");


                        if (split_code[0].equals("l")) {
                            section = "Part " + split_code[split_code.length - 1];
                        } else {
                            section = split_code[split_code.length - 1];
                        }


                        Log.e("EEEE", "readTitleText for heading section: " + section);

                    }


                } else {
                    Log.e("EEEE", "heading section NO MATCH REGEX: " + section);
                }

                Log.e("PRE-heading", "" + parser.getName() + ", " + section + ", " + pinpoint + ")");

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

            } else if (parser.getName().equals("FormulaGroup")) {

                readFormulaGroup(parser, section, pinpoint);


            } else if (parser.getName().equals("Subsection")) {

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");
                    pinpoint = "(" + code[3] + ")";
                }

                Log.e("EEEE", "readSubsection(): " + section + " " + pinpoint);

                readSubsection(parser, section, pinpoint);

            } else if (parser.getName().equals("ContinuedSectionSubsection")) {

                Log.e("EEEE", "readContinuedSectionSubsection(): " + section + " " + section);

                readContinuedSectionSubsection(parser, section, section);


            } else if (parser.getName().equals("Paragraph")) {

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");
                    pinpoint = "(" + code[code.length - 1] + ")";
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


            if (parser.getName().equals("Text")) {

                readSubsectionText(parser, section, pinpoint);

            } else if (parser.getName().equals("MarginalNote")) {

                readSubMarginalText(parser, section, pinpoint);

            } else if (parser.getName().equals("Definition")) {

                readDefinition(parser, section, pinpoint);

            } else if (parser.getName().equals("FormulaGroup")) {

                readFormulaGroup(parser, section, pinpoint);


            } else if (parser.getName().equals("Paragraph")) {

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");
                    pinpoint = "(" + code[(code.length - 1)] + ")";
                }

                Log.e("EEEE", "readSubsectionParagraph(): " + section + " " + pinpoint);

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

    // Parses the contents of a Subsection.
    private void readContinuedSectionSubsection(XmlPullParser parser, String section, String pinpoint) throws IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readContinuedSectionSubsectionText(parser, section, pinpoint);

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

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }


            if (parser.getName().equals("Text")) {

                readSubsectionParagraphText(parser, section, pinpoint);

            } else if (parser.getName().equals("Subparagraph")) {

                Log.e("XML", "Subsection Subparagraph");

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");
                    pinpoint = "(" + code[7] + ")";
                }

                readSubsectionSubParagraph(parser, section, pinpoint);

            } else if (parser.getName().equals("ContinuedParagraph")) {

                Log.e("XML", "readContinuedSubsectionParagraph");

                readContinuedSubsectionParagraph(parser, section, pinpoint);


            } else if (parser.getName().equals("FormulaGroup")) {

                readFormulaGroup(parser, section, pinpoint);

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

            } else if (parser.getName().equals("Clause")) {

                Log.e("eeee", "found clause");

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");

                    pinpoint = "(" + code[(code.length - 1)] + ")";
                }


                Log.e("eeee", "calling readSubparagraphClause");

                readSubparagraphClause(parser, section, pinpoint);


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

            } else if (parser.getName().equals("Clause")) {

                Log.e("eeee", "found clause");

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");

                    pinpoint = "(" + code[(code.length - 1)] + ")";
                }


                Log.e("eeee", "calling readSubparagraphClause for readSubsectionSubParagraph");

                readSubparagraphClause(parser, section, pinpoint);

            } else if (parser.getName().equals("ContinuedSubparagraph")) {

                readContinuedSubsectionSubparagraph(parser, section, pinpoint);

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

    // For Subsection Continued Subsection Paragraph values
    private void readContinuedSubsectionSubparagraph(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readContinuedSubsectionSubparagraphText(parser, section, pinpoint);

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

                readHistorySmallText(parser, section);

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


    // For Clause in Subparagraph
    private void readSubparagraphClause(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readSubparagraphClauseText(parser, section, pinpoint);

            } else if (parser.getName().equals("Subclause")) {

                if ((parser.getAttributeValue(null, "Code")) != null) {
                    String[] code = parser.getAttributeValue(null, "Code").split("\"");

                    pinpoint = "(" + code[(code.length - 1)] + ")";
                }

                Log.e("eeee", "SKIP -- not -- pinpoit subclause:" + pinpoint);

                readSubclause(parser, section, pinpoint);

            } else {
                skip(parser);
            }
        }
    }

    // For Clause in Subparagraph
    private void readSubclause(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readSubclauseText(parser, section, pinpoint);

            } else if (parser.getName().equals("Subsubclause")) {

                    readSubsubclause(parser, section, pinpoint);

            } else {
                skip(parser);
            }
        }
    }

    // For Clause in Subparagraph
    private void readSubsubclause(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readSubsubclauseText(parser, section, pinpoint);


            } else {
                skip(parser);
            }
        }
    }


    // for formula group (in income tax act)
    private void readFormulaGroup(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        Log.e("eeee", "in readFormulaGroup");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            Log.e("eeee", "in while parser.getName()" + parser.getName());


            if (parser.getName().equals("Formula")) {

                Log.e("eeee", "FORMULA  FOUND");

                readFormula(parser, section, pinpoint);

            } else if (parser.getName().equals("FormulaConnector")) {

                Log.e("eeee", "FORMULA CONNECTOR FOUND");

                readDefinitionText(parser, section, pinpoint);


            } else if (parser.getName().equals("FormulaDefinition")) {

                Log.e("eeee", "FORMULA DEFINITION FOUND");

                readFormulaDefinition(parser, section, pinpoint);


            } else {

                skip(parser);
            }
        }
    }

    // for formula
    private void readFormula(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        Log.e("eeee", "in readFormula");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            Log.e("eeee", "in while parser.getName()" + parser.getName());


            if (parser.getName().equals("FormulaText")) {

                Log.e("eeee", "FORMULATEXT FOUND");

                readFormulaText(parser, section, pinpoint);

            } else {

                skip(parser);
            }
        }
    }

    // for formula definition (in income tax act)
    private void readFormulaDefinition(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        Log.e("eeee", "in readFormulaDefinition");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            Log.e("eeee", "in while parser.getName()" + parser.getName());


            if (parser.getName().equals("FormulaTerm")) {

                Log.e("eeee", "FORMULATERM  FOUND");

                readFormulaDefinitionText(parser, section, pinpoint);

            } else if (parser.getName().equals("Text")) {


                Log.e("eeee", "FORMULA DEFINITION TEXT  FOUND");

                readDefinitionText(parser, section, pinpoint);


            } else if (parser.getName().equals("FormulaParagraph")) {


                Log.e("eeee", "FORMULA PARAGRAPH  FOUND");

                readFormulaParagraph(parser, section, pinpoint);

            } else if (parser.getName().equals("ContinuedFormulaParagraph")) {

                Log.e("eeee", "FORMULA PARAGRAPH  FOUND");

                readContinuedParagraph(parser, section, pinpoint);

            } else {

                skip(parser);
            }
        }
    }

    // for formula paragraph (in income tax act)
    private void readFormulaParagraph(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        Log.e("eeee", "in readFormulaParagraph");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            Log.e("eeee", "readFormulaParagraph in while parser.getName()" + parser.getName());


            if (parser.getName().equals("Label")) {

                Log.e("eeee", "Label  FOUND");

                readFormulaParagraphLabel(parser, section);

            } else if (parser.getName().equals("FormulaParagraph")) {

                Log.e("eeee", "Label  FOUND");

                readFormulaParagraphParagraph(parser, section);

            } else {

                skip(parser);
            }
        }
    }

    // for formula paragraph paragraph (in income tax act)
    private void readFormulaParagraphParagraph(XmlPullParser parser, String section) throws
            IOException, XmlPullParserException {

        Log.e("eeee", "in readFormulaParagraph");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            Log.e("eeee", "readFormulaParagraph in while parser.getName()" + parser.getName());


            if (parser.getName().equals("Label")) {

                Log.e("eeee", "Label  FOUND");

                readFormulaParagraphParagraphLabel(parser, section);


            } else {

                skip(parser);
            }
        }
    }


    // for formula continuedClause (in income tax act)
    private void readContinuedClause(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        Log.e("eeee", "in readContinuedClause");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            Log.e("eeee", "readContinuedClause in while parser.getName()" + parser.getName());


            if (parser.getName().equals("Text")) {

                readContinuedClauseText(parser, section, pinpoint);


            } else {

                skip(parser);
            }
        }
    }

    // for formula continuedsubClause (in income tax act)
    private void readContinuedSubclause(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        Log.e("eeee", "in readContinuedSubclause");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            Log.e("eeee", "readContinuedSubclause in while parser.getName()" + parser.getName());


            if (parser.getName().equals("Text")) {

                readContinuedSubclauseText(parser, section, pinpoint);


            } else {

                skip(parser);
            }
        }
    }

    // for footnotes
    private void readFootnote(XmlPullParser parser) throws
            IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readFootnoteText(parser);

            } else {

                skip(parser);
            }
        }
    }

    // for footnotes
    private void readReaderNote(XmlPullParser parser) throws
            IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Note")) {

                readHistorySmallText(parser, "ReaderNote");

            } else {

                skip(parser);
            }
        }
    }

    // for footnotes
    private void readProvision(XmlPullParser parser, String section) throws
            IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("Text")) {

                readProvisionText(parser, section);

            } else {

                skip(parser);
            }
        }
    }


    // for <Identification>
    private void readIdentification(XmlPullParser parser) throws
            IOException, XmlPullParserException {

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("LongTitle")) {

                readLongTitle(parser);

            } else {

                skip(parser);
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

        //  Log.e("XML", "db add readMarginalText (  1  , " + "marginalNote" + section + " " + text + " )");

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

        //   Log.e("XML", "db add readContinuedSubsectionText (  14  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For Subsection Continued Subsection text values.
    private void readContinuedSectionSubsectionText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(14, pinpoint, section, text);

        Log.e("RESULT OBJ", "continuedsst" + resultObject + "" + resultObject.getSection());

        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readContinuedSectionSubsectionText (  14  , " + pinpoint + section + " " + text + " )");

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

        //   Log.e("XML", "db add readSubMarginalText (  5  , " + pinpoint + section + " " + text + " )");

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

        //   Log.e("XML", "db add readSubsectionText (  3  , " + pinpoint + section + " " + text + " )");

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

        //    Log.e("XML", "db add readContinuedParagraphText (  12  , " + pinpoint + section + " " + text + " )");

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

        //    Log.e("XML", "db add Continued Subsection Paragraph Text (  13  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For Continued Subsection Paragraph text values.
    private void readContinuedSubsectionSubparagraphText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(18, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readContinuedSubsectionSubparagraphText (  18  , " + pinpoint + section + " " + text + " )");

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

        //     Log.e("XML", "db add SubParagraph Text (  7  , " + pinpoint + section + " " + text + " )");

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

        //     Log.e("XML", "db add Subsection SubParagraph Text (  8  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }

    }


    // For History List Item Text
    private List readHistorySmallText(XmlPullParser parser, String section) throws IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        if (text != null) {
            // if the history item starts with a space, remove it
            text = text.startsWith(" ") ? text.substring(1) : text;

            Section resultObject = new Section(9, "nopinpoint", section, text);
            dbHelper.insertSectionDetail(resultObject);

            //         Log.e("XML", "db add Historical Note (  9  , " + "nopinpoint" + section + " " + text + " )");
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

        Section resultObject = new Section(2, "section text", section, text);
        dbHelper.insertSectionDetail(resultObject);

        //    Log.e("XML", "db add Section Text (  2  , " + "section text" + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }


    }

    // For the Definition MarginalNote value.
    private void readDefinitionMarginalNoteText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        // Skip tags within the marginal note, grab only text
        // TODO: consider a diff approach
        if (text != null) {

            Section resultObject = new Section(10, pinpoint, section, text);
            dbHelper.insertSectionDetail(resultObject);

            //      Log.e("XML", "db add Definition Marginal Note (  10  , " + pinpoint + section + " " + text + " )");

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

        //   Log.e("XML", "db add readDefinitionTextBEFORE (  11  , " + pinpoint + section + " " + text + " )");

        Section resultObject = new Section(11, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);


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

        Log.e("XML", "db add readSubsectionParagraphText (  6  , " + pinpoint + section + " " + text + " )");

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
            Log.e("XML", "db add readParagraphText SKIPPING");
            skip(parser);
        }
    }

    // For subparagraph clause text values.
    private void readSubparagraphClauseText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(15, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readSubparagraphClauseText (  15  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For subparagraph clause text values.
    private void readSubclauseText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(17, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readSubclauseText (  17  , " + pinpoint + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For subsubclause text values.
    private void readSubsubclauseText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(19, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        Log.e("XML", "db add readSubclauseText (19, " + pinpoint + section + " " + text + ")");

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

        Log.e("XML", "db add heading TitleText (  0  , " + pinpoint + "," + section + " " + text + " )");

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }


    }

    // For the tags FormulaText
    private void readFormulaText(XmlPullParser parser, String section, String pinpoint) throws IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();
        Log.e("XML", "db add readFormulaText ( 16, " + pinpoint + section + text + ")");
        Section resultObject = new Section(16, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);
//
//        Log.e("XML", "db add readFormulaText (  nothing doing for now )");
//
        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }


    }


    // For the Definition MarginalNote value.
    private void readFormulaDefinitionText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();


        Section resultObject = new Section(10, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);


        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }

    }

    // For the tags FormulaText
    private void readFormulaParagraphLabel(XmlPullParser parser, String section) throws IOException, XmlPullParserException {

        Log.e("eeee", "in readFormulaParagraphLabel");

        parser.next();
        String pinpoint = parser.getText();
        String text = "";


        Log.e("eeee", "1 readFormulaParagraphLabel after label .getName()" + parser.getName() + " .getText()" + parser.getText());

        parser.next();

        Log.e("eeee", "2 readFormulaParagraphLabel after label .getName()" + parser.getName() + " .getText()" + parser.getText());

        parser.next();

        Log.e("eeee", "3 readFormulaParagraphLabel after label .getName()" + parser.getName() + " .getText()" + parser.getText());

        parser.next();

        Log.e("eeee", "4 readFormulaParagraphLabel after label .getName()" + parser.getName() + " .getText()" + parser.getText());


        text = parser.getText();


        Log.e("XML", "db add readFormulaParagraphLabel ( 6, " + pinpoint + " " + section + text + ")");
        Section resultObject = new Section(6, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }


    }

    // For the tags FormulaText
    private void readFormulaParagraphParagraphLabel(XmlPullParser parser, String section) throws IOException, XmlPullParserException {

        Log.e("eeee", "in readFormulaParagraphParagraphLabel");

        parser.next();
        String pinpoint = parser.getText();
        String text = "";


        Log.e("eeee", "1 readFormulaParagraphParagraphLabel after label .getName()" + parser.getName() + " .getText()" + parser.getText());

        parser.next();

        Log.e("eeee", "2 readFormulaParagraphParagraphLabel after label .getName()" + parser.getName() + " .getText()" + parser.getText());

        parser.next();

        Log.e("eeee", "3 readFormulaParagraphParagraphLabel after label .getName()" + parser.getName() + " .getText()" + parser.getText());

        parser.next();

        Log.e("eeee", "4 readFormulaParagraphParagraphLabel after label .getName()" + parser.getName() + " .getText()" + parser.getText());


        text = parser.getText();


        Log.e("XML", "db add readFormulaParagraphParagraphLabel ( 8, " + pinpoint + " " + section + text + ")");
        Section resultObject = new Section(8, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);

        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }


    }

    // For the ContinuedClauseText value.
    private void readContinuedClauseText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();


        Section resultObject = new Section(20, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);


        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }
    }

    // For the ContinuedClauseText value.
    private void readContinuedSubclauseText(XmlPullParser parser, String section, String pinpoint) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();


        Section resultObject = new Section(21, pinpoint, section, text);
        dbHelper.insertSectionDetail(resultObject);


        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }

    }

    // For the Footnote value.
    private void readFootnoteText(XmlPullParser parser) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();


        Section resultObject = new Section(9, "Footnote", "Footnote", "* " + text);
        dbHelper.insertSectionDetail(resultObject);


        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }

    }

    // For the Footnote value.
    private void readProvisionText(XmlPullParser parser, String section) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(18, "Provision", section, text);
        dbHelper.insertSectionDetail(resultObject);


        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }

    }


    // For the LongTitle value.
    private void readLongTitle(XmlPullParser parser) throws
            IOException, XmlPullParserException {

        parser.next();

        String text = parser.getText();

        Section resultObject = new Section(1, "", "LongTitle", text);
        dbHelper.insertSectionDetail(resultObject);


        if (parser.next() == XmlPullParser.START_TAG) {
            skip(parser);
        }

    }

}

