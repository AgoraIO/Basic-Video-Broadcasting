import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import { ContainerProvider } from './utils/container';
import { ThemeProvider } from '@material-ui/styles';
import { createMuiTheme } from '@material-ui/core/styles';

const THEME = createMuiTheme({
  typography: {
   "fontFamily": '-apple-system, BlinkMacSystemFont, \\"Segoe UI\\", \\"Roboto\\", \\"Oxygen\\", \\"Ubuntu\\", \\"Cantarell\\", \\"Fira Sans\\", \\"Droid Sans\\", \\"Helvetica Neue\\", sans-serif',
   "fontSize": 14,
   "fontWeightLight": 300,
   "fontWeightRegular": 400,
   "fontWeightMedium": 500
  }
});

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
