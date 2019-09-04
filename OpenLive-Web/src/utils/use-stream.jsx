import React, {useContext, useMemo, useEffect} from 'react';
import {useGlobalState, useGlobalMutation} from './container';

export default function useStream (client) {
  const stateCtx = useGlobalState();
  const mutationCtx = useGlobalMutation();

  const streamList = useMemo(() => {
    return stateCtx.streams;
  });

  const localStream = useMemo(() => {
    return stateCtx.localStream;
  });

  useEffect(() => {
    const addLocal = (evt) => {
      const {stream} = evt;
      const id = stream.getId();
      mutationCtx.setLocalStream(stream);
    }

    const addStream = (evt) => {
      const {stream} = evt;
      const id = stream.getId();
      const _streamList = streamList;
      _streamList.push(stream);
      mutationCtx.setStreamList(_streamList);
    }

    const addRemoteStream = (evt) => {
      const {stream} = evt;
      client.subscribe(stream, (err) => {
        console.error(`stream ${evt.stream.getId()} subscribe failed: ${err}`);
      });
    }

    const subscribeStream = (evt) => {
      const {stream} = evt;
      const id = stream.getId();
      const _streamList = streamList;
      _streamList.push(stream);
      mutationCtx.setStreamList(_streamList);
    }

    const removeStream = (evt) => {
      const {stream} = evt;
      const id = stream ? stream.getId() : evt.id;
      const _streamList = streamList;
      const index = _streamList.findIndex(item => item.getId() === id);
      if (index !== -1) {
        mutationCtx.setStreamList(_streamList.filter((stream, idx) => (idx !== index)));
      }
    }

    if (client) {
      client.on("localStream-added", addLocal);
      client.on("stream-published", addStream);
      client.on("stream-added", addRemoteStream);
      client.on("stream-removed", removeStream);
      client.on("stream-subscribed", subscribeStream);
      client.on("peer-leave", removeStream);
    }
    return () => {
      if (client) {
        client.destroy();
      }
    }
  }, [])

  return [localStream, streamList];
}