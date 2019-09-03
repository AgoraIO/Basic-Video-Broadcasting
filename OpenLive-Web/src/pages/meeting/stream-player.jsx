import React, {useState, useContext, useEffect} from 'react';
import PropTypes from 'prop-types';

StreamPlayer.propTypes = {
  stream: PropTypes.object,
}

export default function StreamPlayer (props) {
  const {stream} = props;
  const uid = stream.getId();

  const domId = `stream-player-${uid}`;

  useEffect(() => {
    if (!stream.isPlaying()) {
      stream.play(domId)
    }
    return () => {
      if (stream.isPlaying()) {
        stream.stop();
        stream.close();
      }
    }
  }, [stream])

  return (
    <div className="stream-player" id={domId}>
      {props.children}
    </div>
  )
}