import React from 'react';
import {useGlobalState, useGlobalMutation} from '../../utils/container';
import useDevices from '../../utils/use-devices';
import PropTypes from 'prop-types';
import { makeStyles, withStyles } from '@material-ui/core/styles';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import InputLabel from '@material-ui/core/InputLabel';
import Box from '@material-ui/core/Box';
import MenuItem from '@material-ui/core/MenuItem';
import Switch from '@material-ui/core/Switch';
import {NavLink} from 'react-router-dom';

SettingsCard.propTypes = {
  name: PropTypes.string,
  resolution: PropTypes.string,
  cameraDevice: PropTypes.string,
  microphoneDevice: PropTypes.string,
  video: PropTypes.bool,
  audio: PropTypes.bool,
};

const useStyles = makeStyles(theme => ({
  backBtn: {
    '&:hover': {
      backgroundImage: 'url("/icon-back-hover.png")',
    },
    backgroundSize: '32px',
    backgroundImage: 'url("/icon-back.png")',
    backgroundRepeat: 'no-repeat',
    top: '1rem',
    height: '32px',
    position: 'absolute',
    width: '32px',
    left: '1rem',
    cursor: 'pointer',
    zIndex: '2'
  },
  menuTitle: {
    color: '#333333',
    textAlign: 'center',
    fontSize: 'h6.fontSize',
    position: 'relative',
    top: '7px',
  },
  marginTop: {
    marginTop: "0 !important"
  },
  menu: {
    margin: '0.4rem 0',
    position: 'relative',
    height: '39px',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center'
  },
  line: {
    marginTop: '0.2rem',
    marginBottom: '0.5rem',
    borderBottom: '1px solid #EAEAEA'
  },
  hr: {
    borderBottom: '1px solid #EAEAEA'
  },
  switchItem: {
    flexDirection: 'row-reverse !important',
    marginLeft: '0 !important',
    marginRight: '0 !important',
    justifyContent: 'space-between',
  }
}));

const CustomSwitch = withStyles(theme => ({
  root: {
    width: 42,
    height: 26,
    padding: 0,
    margin: theme.spacing(1),
  },
  switchBase: {
    padding: 1,
    '&$checked': {
      transform: 'translateX(16px)',
      color: theme.palette.common.white,
      '& + $track': {
        backgroundColor: '#44A2FC',
        opacity: 1,
        border: 'none',
      },
    },
    '&$focusVisible $thumb': {
      color: '#52d869',
      border: '6px solid #fff',
    },
  },
  thumb: {
    width: 24,
    height: 24,
  },
  track: {
    borderRadius: 26 / 2,
    border: `1px solid ${theme.palette.grey[400]}`,
    backgroundColor: theme.palette.grey[50],
    opacity: 1,
    transition: theme.transitions.create(['background-color', 'border']),
  },
  checked: {},
  focusVisible: {},
}))(({ classes, ...props }) => {
  return (
    <Switch
      focusVisibleClassName={classes.focusVisible}
      disableRipple
      classes={{
        root: classes.root,
        switchBase: classes.switchBase,
        thumb: classes.thumb,
        track: classes.track,
        checked: classes.checked,
      }}
      {...props}
    />
  );
});

export default function SettingsCard() {
  const classes = useStyles();

  const stateCtx = useGlobalState();

  const mutationCtx = useGlobalMutation();

  const [ cameraList, microphoneList ] = useDevices();

  return (
    <Box flex="1" display="flex" flexDirection="column">
      <NavLink to="/" className={classes.backBtn} />
      <Box display="flex" flex="1" flexDirection="column" padding="0 1rem" justifyContent="flex-start">
      <FormControl className={classes.menu}>
        <span className={classes.menuTitle}>Setting</span>
      </FormControl>
      <div className={classes.line}></div>
      {/* <FormControl className={classes.marginTop}>
        <InputLabel htmlFor="name">Channel Name</InputLabel>
        <Input id="name"defaultValue={stateCtx.config.channelName} onChange={(evt) => {
            mutationCtx.updateConfig({channelName: evt.target.value})
        }}/>
      </FormControl> */}
      <FormControl>
        <InputLabel htmlFor="resolution">Resolution</InputLabel>
        <Select
          value={stateCtx.config.resolution}
          onChange={(evt) => {
            mutationCtx.updateConfig({
              resolution: evt.target.value,
            })
          }}
          inputProps={{
            name: 'resolution',
            id: 'resolution',
          }}
        >
          <MenuItem value={"480p"}>480p</MenuItem>
          <MenuItem value={"720p"}>720p</MenuItem>
          <MenuItem value={"1080p"}>1080p</MenuItem>
        </Select>
      </FormControl>
      <FormControl>
        <InputLabel htmlFor="codec">Video Codec</InputLabel>
        <Select
          value={stateCtx.codec}
          onChange={(evt) => {
            mutationCtx.setCodec(evt.target.value)
          }}
          inputProps={{
            name: 'codec',
            id: 'codec',
          }}
        >
          <MenuItem value={"h264"}>h264</MenuItem>
          <MenuItem value={"vp8"}>vp8</MenuItem>
        </Select>
      </FormControl>
      <FormControl>
        <InputLabel htmlFor="camera">Camera</InputLabel>
        <Select
          value={stateCtx.config.cameraId}
          onChange={(evt) => {
            mutationCtx.updateConfig({
              cameraId: evt.target.value
            })
          }}
          inputProps={{
            name: 'camera',
            id: 'camera',
          }}
        >
          {cameraList.map(
            (item, key) =>
              <MenuItem
                key={key}
                value={item.value}
              >{item.label}</MenuItem>
          )}
        </Select>
      </FormControl>
      <FormControl>
        <InputLabel htmlFor="Microphone">Microphone</InputLabel>
        <Select
          value={stateCtx.config.microphoneId}
          onChange={(evt) => {
            mutationCtx.updateConfig({
              microphoneId: evt.target.value
            })
          }}
          inputProps={{
            name: 'microphone',
            id: 'microphone',
          }}
        >
          {microphoneList.map(
            (item, key) =>
              <MenuItem
                key={key}
                value={item.value}
              >{item.label}</MenuItem>
          )}
        </Select>
      </FormControl>
      <FormControl>
        <FormControlLabel
          control={
            <CustomSwitch
              checked={stateCtx.video}
              onChange={() => {
                mutationCtx.setVideo(!stateCtx.video)
              }}
              value={stateCtx.video}
              color="primary"
            />
          }
          className={classes.switchItem}
          label="Video"
        />
        <div className={classes.hr}></div>
      </FormControl>
      <FormControl>
        <FormControlLabel
          control={
            <CustomSwitch
              checked={stateCtx.audio}
              onChange={() => {
                mutationCtx.setAudio(!stateCtx.audio)
              }}
              value={stateCtx.audio}
              color="primary"
            />
          }
          className={classes.switchItem}
          label="Audio"
        />
        <div className={classes.hr}></div>
      </FormControl>
    </Box>
    </Box>
  )
}