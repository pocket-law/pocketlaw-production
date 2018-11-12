import React, {Component} from 'react';
import {AppRegistry, Text, View, ListView, StyleSheet} from 'react-native';

const jsonString = '';

const mDictJson = require('./json/dict.json');

var mStatute = require('./html/test.html');

// This variable is used to avoid searching again when clicking the hamburger menu after a search
const lastSearch = '';

var DomParser = require('react-native-html-parser').DOMParser

export default class MainListView extends Component{
    constructor(){
        super();
        const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        this.state = {
            termDataSource: ds,
            resultsArray: [],
            searchTerm:  ''

        };
    }


    componentWillReceiveProps(nextProps) {

    }

    componentDidMount(){
        this.getInternalJson();



        let html =
            `<!DOCTYPE html>
                <html>
                    <body>
                        <div id="b">
                            <a href="example.org">EXAMPLE</a>
                            <div class="inA">
                                <br>bbbb</br>
                            </div>
                            <ul>
                                <li><a href="http://justice.gc.ca/eng/fl-df/index.html">Family Law</a></li>
                                <li><a href="http://justice.gc.ca/eng/cj-jp/index.html">Criminal Justice</a></li>
                                <li><a href="http://justice.gc.ca/eng/fund-fina/index.html">Funding</a></li>
                                <li><a href="http://justice.gc.ca/eng/csj-sjc/">Canada's System of Justice</a></li>
                                <li><a href="http://laws-lois.justice.gc.ca/eng/">Laws</a></li>
                            </ul>
                        </div>
                        <div class="bb">
                            Test
                        </div>
                    </body>
                </html>`;

        //let html = mStatute;

        // const html = require('./html/test.html');

        let doc = new DomParser().parseFromString(html,'text/html');

        //console.log("doc.querySelect('#b .inA'): \n" + doc.querySelect('#b .inA'));
        console.log("doc.getElementsByTagName('a'): \n" + doc.getElementsByTagName('a'));
        //console.log("doc.querySelect('#b a[href=\"example.org\"]'): \n" + doc.querySelect('#b a[href="example.org"]'));

        //let htmlDoc = new DomParser().parseFromString(mStatute,'text/html');

        //console.log(doc.getElementsByTagName('a')[0]);

        const oompa = require('./html/test.html');

        console.log("oyoyo " + oompa);

        console.log("HEREWEALLARE: " + html)

    }

    // TODO: remove this json jazz.
    getInternalJson(){
        this.setState({
            termDataSource: this.state.termDataSource.cloneWithRows(mDictJson.terms)
        });

        jsonString = JSON.stringify(mDictJson);
    }


    renderRow(term, sectionId, rowId, highlightRow){
        return(
            <View>
                <Text>ROW TEXT and TERM {term.term} </Text>
            </View>
        )
    }

    render(){
        return(
            <ListView
                style={styles.listView}
                ref='mainListviewRef'
                dataSource={this.state.termDataSource}
                renderRow={this.renderRow.bind(this)} />
        );
    }
}


const styles = StyleSheet.create({
    listView: {

    }
});

AppRegistry.registerComponent('MainListView', () => MainListView);
