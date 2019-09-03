import React, { useContext, useEffect } from 'react';
import { useGlobalState, useGlobalMutation } from '../utils/container';
import { makeStyles } from '@material-ui/core/styles';
import { Container, Box } from '@material-ui/core';
import IndexCard from './index/card';

const useStyles = makeStyles(() => ({
  container: {
    height: '100%',
    width: '100%',
    minWidth: 800,
    minHeight: 600,
    boxSizing: 'content-box',
    display: 'flex'
  }
}));

const Index = () => {
  const stateCtx = useGlobalState();
  const mutationCtx = useGlobalMutation();
  const classes = useStyles();

  useEffect(() => {
    if (stateCtx.loading === true) {
      mutationCtx.stopLoading();
    }
  });

  return (
    <Container maxWidth="sm" className={classes.container}>
      <IndexCard />
    </Container>
  )
};

export default Index;