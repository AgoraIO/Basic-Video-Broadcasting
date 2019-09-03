import React, {useState, useContext, useEffect} from 'react';
import clsx from 'clsx';
import { makeStyles } from '@material-ui/core/styles';
import {useGlobalState, useGlobalMutation} from '../../utils/container';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Input from '@material-ui/core/Input';
import Card from '@material-ui/core/Card';
import Box from '@material-ui/core/Box';
import Button from '@material-ui/core/Button';
import SettingsCard from './settings-card';
import useRouter from '../../utils/use-router';

const useStyles = makeStyles(theme => ({
  backBtn: {
    '&:hover': {
      backgroundImage: 'url("./icon-back-hover.png")',
    },
    '&::before': {
      backgroundColor: 'rgba(0, 0, 0, 0.37)',
      content: ' ',
      display: 'block',
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
    width: '260px',
    '&:hover': {
      backgroundColor: '#307AFF',
    },
    margin: theme.spacing(1),
    backgroundColor: '#44a2fc',
    borderRadius: '20px'
  }
}));

function CardComponent () {
  const classes = useStyles();

  const routerCtx = useRouter();
  const stateCtx = useGlobalState();
  const mutationCtx = useGlobalMutation();

  const [entryCard, switchCard] = useState(true);

  const handleClick = () => {
    if (!stateCtx.config.channelName) {
      mutationCtx.toastError('频道名不能为空')
    }
    mutationCtx.startLoading();
    routerCtx.history.push(`/meeting/${stateCtx.config.channelName}`);
  }

  return (
    <>
      {entryCard ?
      <Box flex="1" display="flex" alignItems="center" justifyContent="center" flexDirection="column">
        <i className={classes.settingBtn} onClick={() => {
          switchCard(false);
        }}/>
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
      </Box> : 
      <Box flex="1" display="flex" flexDirection="column">
        <i className={classes.backBtn} onClick={() => {
          switchCard(true);
        }}/>
        <SettingsCard />
      </Box>
      }
    </>
  )
}

export default function IndexCard() {
  const classes = useStyles();

  return (
    <Box display="flex" alignItems="center" justifyContent="center">
      <Card className={classes.card}>
        <Box display="flex" flex="1">
          <div className={classes.coverLeft}>
            <div className={classes.item}>
              <div className={classes.coverImage} />
            </div>
            <div className={classes.item}>
              <div className={classes.coverContent}>
                <Box textAlign="center" fontSize="h6.fontSize" className={classes.fontStyle}>Welcome to</Box>
                <Box textAlign="center" fontWeight="fontWeightRegular" fontSize="h4.fontSize" className={classes.midItem}>OPEN LIVE</Box>
                <Box textAlign="center" fontWeight="fontWeightRegular" className={classes.fontStyle} fontSize="h7.fontSize">Powered by Agora.io</Box>
              </div>
            </div>
          </div>
          <div className={classes.coverRight}>
            <CardComponent />
          </div>
        </Box>
      </Card>
    </Box>
  )
}