import React from 'react';
import './App.css';
import { Switch, Route } from 'react-router-dom';
import Index from './pages/index';
import Meeting from './pages/meeting';
import {BrowserRouterHook} from './utils/use-router';
function App() {
  return (
    <BrowserRouterHook>
      <Route exact path="/" component={Index}></Route>
      <Route exact path="/meeting/:name" component={Meeting}></Route>
    </BrowserRouterHook>
  );
}

export default App;
