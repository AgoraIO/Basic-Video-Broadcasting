import React from 'react';
import './App.css';
import { Switch, Route } from 'react-router-dom';
import Index from './pages/index';
import Meeting from './pages/meeting';
import {BrowserRouterHook} from './utils/use-router';

const routes = [
  {
    Component: (props) => <Index {...props} />,
    path: '/'
  },
  {
    Component: (props) => <Meeting {...props} />,
    path: '/meeting/:name'
  }
];

function App() {
  return (
    <BrowserRouterHook>
      <Switch>
        <Route exact path="/" component={Index}></Route>
        <Route exact path="/meeting/:name" component={Meeting}></Route>
      </Switch>
    </BrowserRouterHook>
  );
}

export default App;
