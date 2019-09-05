import React, {useContext} from "react";
import { HashRouter as Router, Route} from 'react-router-dom';

export const RouterContext = React.createContext({});

export const BrowserRouterHook = ({ children }) => (
  <Router>
    <Route>
      {(routeProps) => (
        <RouterContext.Provider value={routeProps}>
          {children}
        </RouterContext.Provider>
      )}
    </Route>
  </Router>
);

export default function useRouter() {
  return useContext(RouterContext);
}