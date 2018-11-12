import React, {Component} from 'react';
import {AppRegistry, Text, View, ListView, StyleSheet, TouchableHighlight} from 'react-native';

const mDictJson = require('./json/dict.json');

export default class CatsListView extends Component{
    constructor(){
        super();
        const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        this.state = {
            catsDataSource: ds,
            categorySet: ''
        };
    }


    componentDidMount(){
        this.getInternalJson();
    }

    handlePress(category) {
        this.state.categorySet = category.name;
        this.props.changeCategory(category.name);
    }

    getInternalJson(){
        this.setState({
            catsDataSource: this.state.catsDataSource.cloneWithRows(mDictJson.categories)
        });

        jsonString = JSON.stringify(mDictJson);
    }

    renderRow(category, sectionId, rowId, highlightRow){
            return(
                <TouchableHighlight onPress={()=>this.handlePress(category)}>
                    <View style={styles.row}>
                        <View>
                            <Text style={styles.categoryText}>{category.name}</Text>
                        </View>
                    </View>
                </TouchableHighlight>
            )
        }

    render(){
        return(
            <ListView
                style={styles.listView}
                dataSource={this.state.catsDataSource}
                renderRow={this.renderRow.bind(this)} />
        );
    }
}


const styles = StyleSheet.create({
    listView: {

    },

    row: {
        flexDirection: 'row',
        justifyContent: 'center',
        padding: 10,
        backgroundColor: '#2e3f76',
        marginBottom: 3
    },

    categoryText: {
        flex: 1,
        fontSize: 20,
        color: '#FFFFFF',
        textAlign: 'center',
        fontWeight:'bold'
    }

});

AppRegistry.registerComponent('CatsListView', () => CatsListView);
