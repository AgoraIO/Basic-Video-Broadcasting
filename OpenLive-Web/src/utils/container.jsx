import React, {createContext, useContext, useReducer, useState, useEffect} from 'react';
import {mutations, defaultState} from './store';
import CustomizedSnackbar from '../utils/snackbar-wrapper';
import Loading from '../utils/loading';

const StateContext = createContext({});
const MutationContext = createContext({});

export const ContainerProvider = ({children}) => {
  const [state, dispatch] = useReducer(mutations, defaultState);

  const [toasts, updateToasts] = useState([]);

  const methods = {
    startLoading() {
      dispatch({type: 'loading', payload: true});
    },
    stopLoading() {
      dispatch({type: 'loading', payload: false});
    },
    updateConfig(params) {
      dispatch({type: 'config', payload: {...state.config, ...params}});
    },
    setClient(clientInstance) {
      dispatch({type: 'client', payload: clientInstance});
    },
    setCodec(param) {
      dispatch({type: 'codec', payload: param});
    },
    setVideo(param) {
      dispatch({type: 'video', payload: param});
    },
    setAudio(param) {
      dispatch({type: 'audio', payload: param});
    },
    setScreen(param) {
      dispatch({type: 'screen', payload: param});
    },
    toastSuccess(message) {
      updateToasts([
        ...toasts,
        {
          variant: 'success',
          message
        }
      ]);
    },
    toastInfo(message) {
      updateToasts([
        ...toasts,
        {
          variant: 'info',
          message
        }
      ]);
    },
    toastError(message) {
      updateToasts([
        ...toasts,
        {
          variant: 'error',
          message
        }
      ]);
    },
    removeTop() {
      const items = toasts.filter((e, idx) => idx > 0)
      updateToasts([
        ...items
      ]);
    },
    setLocalStream(param) {
      dispatch({type: 'localStream', payload: param});
    },
    setStreamList(param) {
      console.log(`set stream to lists, ${param.map(e => e.getId())}`);
      dispatch({type: 'streamList', payload: param});
    },
    setDevicesList(param) {
      dispatch({type: 'devicesList', payload: param});
    }
  };

  return (
    <StateContext.Provider value={state}>
      <MutationContext.Provider value={methods}>
        <CustomizedSnackbar toasts={toasts} />
        {state.loading ? <Loading />: null}
        {children}
      </MutationContext.Provider>
    </StateContext.Provider>
  )
};

export function useGlobalState () {
  return useContext(StateContext);
};

export function useGlobalMutation () {
  return useContext(MutationContext);
};