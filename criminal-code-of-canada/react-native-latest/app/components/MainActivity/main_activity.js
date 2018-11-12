import React, {Component} from 'react';
import {AppRegistry, Text, View, StyleSheet, TouchableHighlight, Keyboard, BackAndroid} from 'react-native';

import MainListView from './ListView/main_listview';
import CatsListView from './ListView/cats_listview';
import TitleBar from './TitleBar/title_bar';

export default class MainActivity extends Component{
    constructor(){
        super();
        this.state = {
            isVisible: 'full-list', // 'full-list', 'categories'
            searchTerm: '',
            categorySet: ''
        };
    }

    handleListView(isVisible) {
        this.state.categorySet = ''

        if (isVisible == 'categories') {
            this.setState({isVisible: 'categories'});
        } else if (isVisible == 'full-list') {
            this.setState({isVisible: 'full-list'});
        }
    }

    handleSearch(searchTerm) {
        this.setState({searchTerm: searchTerm});
        this.setState({categorySet: ''});
        this.setState({isVisible: 'full-list'});
        Keyboard.dismiss();
        console.log("MainActivity search: " + searchTerm);
    }

    handleCategoryChange(category) {
        this.setState({categorySet: category});
        this.setState({searchTerm: ''});
        this.setState({isVisible: 'full-list'});
        console.log("MainActivity category: " + category);
    }

    componentDidMount() {
        console.log("BACKK! " + this.state.isVisible);
        BackAndroid.addEventListener('hardwareBackPress', this.handleBack.bind(this));
    }

    componentWillUnmount() {
        //Forgetting to remove the listener will cause pop executes multiple times
        BackAndroid.removeEventListener('hardwareBackPress', this.handleBack.bind(this));
    }

    handleBack() {
        if (this.state.isVisible != 'full-list') {
            this.setState({isVisible: 'full-list'});
            return true; //avoid closing the app
        } else if (this.state.searchTerm != '') {
            this.handleSearch('');
            return true; //avoid closing the app
        } else if (this.state.categorySet != '') {
            this.state.categorySet = '';
            this.setState({isVisible: 'categories'});
            return true; //avoid closing the app
        }

    return false; //close the app
    }

    //RENDER RENDER
    render(){
        return(
            <View style={styles.container}>
                <View style={styles.titleBar}>
                    <TitleBar
                        changeListView={this.handleListView.bind(this)}
                        isVisible={this.state.isVisible}
                        searchTerm={this.state.searchTerm}
                        searchFor={this.handleSearch.bind(this)} />
                </View>
                <View style={styles.bodyView}>
                    {this.state.isVisible == 'full-list' ?
                        <View>
                            <MainListView
                                categorySet={this.state.categorySet}
                                searchTerm={this.state.searchTerm} />
                        </View>
                    :
                        <View style={styles.noHeight}>
                            <MainListView
                                categorySet={this.state.categorySet}
                                searchTerm={this.state.searchTerm} />
                        </View>
                    }
                    {this.state.isVisible == 'categories'  ?
                        <View>
                            <CatsListView
                                changeCategory={this.handleCategoryChange.bind(this)} />
                        </View>
                    :
                    <View style={styles.noHeight}>
                        <CatsListView
                            changeCategory={this.handleCategoryChange.bind(this)} />
                    </View>
                    }
                </View>
                <View style={styles.footer}/>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection:'column'
    },

    bodyView: {
        flex: 1
    },

    noHeight: {
        height: 0
    },

    titleBar: {

    },

    footer: {
        height: 40,
        backgroundColor: '#3B5198'
    }

});

AppRegistry.registerComponent('MainActivity', () => MainActivity);
