import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import { ContainerProvider } from './utils/container';
import { ThemeProvider } from '@material-ui/styles';
import THEME from './utils/theme';

ReactDOM.render(
  <ThemeProvider theme={THEME}>
    <ContainerProvider>
      <App />
    </ContainerProvider>
  </ThemeProvider>
, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
