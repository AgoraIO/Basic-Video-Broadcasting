import React, {useMemo, useState, useEffect} from 'react';
import PropTypes from 'prop-types';

StreamPlayer.propTypes = {
  stream: PropTypes.object,
}

export default function StreamPlayer (props) {
  const {stream} = props;
  const uid = stream.getId();

  const domId = `stream-player-${uid}`;

  const [_resume, changeResume] = useState(false);
  const resume = useMemo(() => {
    return _resume;
  }, [_resume]);

  const [_autoplay, changeAutoPlay] = useState(false);
  const autoplay = useMemo(() => {
    return _autoplay;
  }, [_autoplay]);

  const handleClick = () => {
    if (!resume) {
      stream.resume();
      changeResume(true);
    }
  }

  useEffect(() => {
    if (!stream.isPlaying()) {
      stream.play(domId, {fit: 'cover'}, (errState) => {
        if (errState && errState.status !== 'aborted') {
          console.log("play failed ", domId)
          changeAutoPlay(true);
        }
      })
    }
    return () => {
      if (stream.isPlaying()) {
        stream.stop();
      }
    }
  }, [stream, domId])

  return (
    <div className={`stream-player ${autoplay ? "autoplay": ''}`} id={domId} onClick={handleClick}>
      {props.children}
    </div>
  )
}