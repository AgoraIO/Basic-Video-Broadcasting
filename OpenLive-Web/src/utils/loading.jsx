import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import CircularProgress from '@material-ui/core/CircularProgress';

const useStyles = makeStyles(theme => ({
  progress: {
    margin: theme.spacing(2),
  },
  container: {
    height: '100%',
    width: '100%',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center'
  }
}));

export default function CircularLoading () {
  const classes = useStyles();

  return (
    <div className={classes.container}>
      <CircularProgress className={classes.progress} />
    </div>
  );
}