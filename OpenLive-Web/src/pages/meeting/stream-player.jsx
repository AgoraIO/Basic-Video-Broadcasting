import React, {useMemo, useState, useEffect} from 'react';
import PropTypes from 'prop-types';

StreamPlayer.propTypes = {
  stream: PropTypes.object,
}

export default function StreamPlayer (props) {

  const {stream} = props;

  const uid = stream.getId();

  // const stream = useState(props.stream)[0];
  // const uid = useState(stream.getId())[0];
  // const [stream, uid] = useMemo(() => [props.stream, props.stream.getId()], [props]);

  const domId = `stream-player-${uid}`;

  const [resume, changeResume] = useState(false);

  const [autoplay, changeAutoplay] = useState(false);

  const handleClick = () => {
    if (autoplay && !resume) {
      stream.resume();
      changeResume(true);
    }
  }

  const [isPlaying, setPlaying] = useState(stream.isPlaying());

  useEffect(() => {
    // console.log(`stream-player ${domId} stream.isPlaying(): ${stream.isPlaying()}`)
    if (!isPlaying) {
      stream.play(domId, {fit: 'cover'}, (errState) => {
        if (errState && errState.status !== 'aborted') {
          console.log("stream-player play failed ", domId)
          changeAutoplay(true);
        } else {
          console.log(`${stream.getId()}#played`);
        }
      });
    }
    return () => {
      if (isPlaying) {
        console.log(`${stream.getId()}#stop >>>> ${domId} ${isPlaying}`)
        stream.stop();
      }
    }
  }, [stream, uid, domId]);

  // useEffect(() => {
  //   console.log(`change playing ${domId}`, stream.isPlaying());
  //   setPlaying(stream.isPlaying());
  //   return () => {
  //     setPlaying(stream.isPlaying());
  //     console.log(`final change playing  ${domId}`, stream.isPlaying());
  //   }
  // }, [stream]);

  return (
    <div className={`stream-player ${autoplay ? "autoplay": ''}`} id={domId} onClick={handleClick} onDoubleClick={props.onDoubleClick}>
      {props.children}
      {props.uid ? <div className='stream-uid'>UID: {stream.getId()}</div> : null }
    </div>
  )
}