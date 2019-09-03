import React, {useState, useReducer, useEffect} from 'react';

export default function useStream (client) {
  const [streamList, setStreamList] = useState([]);

  const [localStream, setLocalStream] = useState(null);

  useEffect(() => {
    const addLocal = (evt) => {
      console.log("create local stream success");
      const {stream} = evt;
      const id = stream.getId();
      setLocalStream(stream);
    }

    const addStream = (evt) => {
      const {stream} = evt;
      const id = stream.getId();
      setStreamList(streamList => {
        streamList.push(stream);
        return streamList;
      });
    }

    const removeStream = (evt) => {
      const {stream} = evt;
      const id = stream.getId();
      const index = streamList.findIndex(item => item.getId() === id);
      if (index !== -1) {
        setStreamList(streamList => {
          streamList.push(stream);
          return streamList;
        });
      }
    }

    const subscribeStream = (evt) => {
      client.subscribe(evt.stream);
    }

    if (client) {
      client.on("localStream-added", addLocal);
      client.on("stream-published", addStream);
      client.on("stream-added", addStream);
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