import React, {Component} from 'react';
import {AppRegistry, Text, View, Navigator} from 'react-native';

import MainActivity from './components/MainActivity/main_activity';


export default class Root extends Component{
  renderScene(route, navigator){
    switch(route.id){
      case 'main_activity':
        return (<MainActivity navigator={navigator} title="main_activity" />)
    }
  }

  render(){
    return(
      <Navigator
        initialRoute={{id: 'main_activity'}}
        renderScene={this.renderScene}
        configureScreen={(route, routeStack) => Navigator.SceneConfigs.FloatFromBottom}
      />
    );
  }
}

AppRegistry.registerComponent('Root', () => Root);
