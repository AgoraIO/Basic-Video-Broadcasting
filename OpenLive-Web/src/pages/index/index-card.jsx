import React from 'react';
import clsx from 'clsx';
import { makeStyles } from '@material-ui/core/styles';
import {useGlobalState, useGlobalMutation} from '../../utils/container';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Input from '@material-ui/core/Input';
import Box from '@material-ui/core/Box';
import Button from '@material-ui/core/Button';
import useRouter from '../../utils/use-router';
import {NavLink} from 'react-router-dom';

const useStyles = makeStyles(theme => ({
  backBtn: {
    '&:hover': {
      backgroundImage: 'url("./icon-back-hover.png")',
    },
    backgroundSize: '32px',
    backgroundImage: 'url("./icon-back.png")',
    backgroundRepeat: 'no-repeat',
    top: '1rem',
    height: '32px',
    position: 'absolute',
    width: '32px',
    left: '1rem',
    cursor: 'pointer',
    zIndex: '2'
  },
  settingBtn: {
    '&:hover': {
      backgroundImage: 'url("./icon-setting-hover.png")',
    },
    backgroundImage: 'url("./icon-setting.png")',
    backgroundSize: '32px',
    backgroundRepeat: 'no-repeat',
    top: '1rem',
    height: '32px',
    position: 'absolute',
    width: '32px',
    right: '1rem',
    cursor: 'pointer',
  },
  fontStyle: {
    color: '#9ee2ff',
  },
  midItem: {
    marginTop: '1rem',
    marginBottom: '6rem',
  },
  item: {
    flex: 1,
    display: 'flex',
    alignItems: 'center'
  },
  coverLeft: {
    background: `linear-gradient(to bottom, #307AFF, 50%, #46cdff)`,
    alignItems: 'center',
    flex: 1,
    display: 'flex',
    flexDirection: 'column'
  },
  coverContent: {
    display: 'flex',
    justifyContent: 'center',
    flexDirection: 'column',
    color: '#fff',
  },
  coverImage: {
    marginTop: '4rem',
    width: '180px',
    height: '180px',
    backgroundSize: 'contain',
    backgroundRepeat: 'no-repeat',
    backgroundImage: 'url("./logo-open-live.png")'
  },
  coverRight: {
    position: 'relative',
    flex: 1,
    display: 'flex',
  },
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
  },
  card: {
    display: 'flex',
    minWidth: 700,
    minHeight: 500,
    maxHeight: 500,
    borderRadius: '10px',
    boxShadow: '0px 6px 18px 0px rgba(0,0,0,0.2)'
  },
  input: {
    maxWidth: '250px',
    minWidth: '250px',
    alignSelf: 'center',
  },
  grid: {
    margin: '0 !important',
  },
  button: {
    height: '44px',
    width: '260px',
    '&:hover': {
      backgroundColor: '#307AFF',
    },
    margin: theme.spacing(1),
    marginTop: '33px',
    backgroundColor: '#44a2fc',
    borderRadius: '30px'
  }
}));

export default function IndexCard () {
  const classes = useStyles();

  const routerCtx = useRouter();
  const stateCtx = useGlobalState();
  const mutationCtx = useGlobalMutation();

  const handleClick = () => {

    if (!stateCtx.config.channelName) {
      mutationCtx.toastError(`channelName can't be blank`)
      return;
    }

    mutationCtx.startLoading();
    routerCtx.history.push(`/meeting/${stateCtx.config.channelName}`);
  }

  return (
    <Box flex="1" display="flex" alignItems="center" justifyContent="center" flexDirection="column">
      <NavLink to="/setting" className={classes.settingBtn} />
      <FormControl className={clsx(classes.input, classes.grid)}>
        <InputLabel htmlFor="channelName">Channel Name</InputLabel>
        <Input
          id="channelName"
          name="channelName"
          defaultValue={stateCtx.config.channelName}
          onChange={(evt) => {
            mutationCtx.updateConfig({channelName: evt.target.value})
          }}/>
      </FormControl>
      <FormControl className={classes.grid}>
        <Button onClick={handleClick} variant="contained" color="primary" className={classes.button}>
          Start Live Broadcast
        </Button>
      </FormControl>
    </Box>
  )
}