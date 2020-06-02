import React, { useEffect } from 'react'
import PropTypes from 'prop-types'
import clsx from 'clsx'
import Snackbar from '@material-ui/core/Snackbar'
import CheckCircleIcon from '@material-ui/icons/CheckCircle'
import ErrorIcon from '@material-ui/icons/Error'
import InfoIcon from '@material-ui/icons/Info'
import CloseIcon from '@material-ui/icons/Close'
import { amber, green } from '@material-ui/core/colors'
import IconButton from '@material-ui/core/IconButton'
import SnackbarContent from '@material-ui/core/SnackbarContent'
import WarningIcon from '@material-ui/icons/Warning'
import { makeStyles } from '@material-ui/core/styles'
import { useGlobalMutation } from '../utils/container'

const variantIcon = {
  success: CheckCircleIcon,
  warning: WarningIcon,
  error: ErrorIcon,
  info: InfoIcon
}

const useStyles1 = makeStyles((theme) => ({
  success: {
    backgroundColor: green[600]
  },
  error: {
    backgroundColor: theme.palette.error.dark
  },
  info: {
    backgroundColor: theme.palette.primary.main
  },
  warning: {
    backgroundColor: amber[700]
  },
  icon: {
    fontSize: 20
  },
  iconVariant: {
    opacity: 0.9,
    marginRight: theme.spacing(1)
  },
  message: {
    display: 'flex',
    alignItems: 'center'
  },
  customSnackbar: {
    minWidth: '180px !important',
    minHeight: '40px !important',
    background: 'rgba(0,0,0,0.7)',
    boxShadow: '0px 2px 4px 0px rgba(42,62,84,0.24)',
    borderRadius: '30px',
    justifyContent: 'center',
    padding: '0 11px'
  }
}))

SnackbarWrapper.propTypes = {
  message: PropTypes.string,
  onClose: PropTypes.func,
  variant: PropTypes.oneOf(['error', 'info', 'success', 'warning']).isRequired
}

function SnackbarWrapper (props) {
  const classes = useStyles1()
  const mutationCtx = useGlobalMutation()
  const { message, onClose, variant, ...other } = props
  const Icon = variantIcon[variant]

  useEffect(() => {
    const timer = setTimeout(() => {
      mutationCtx.removeTop()
    }, 1000)
    return () => {
      clearTimeout(timer)
    }
  }, [mutationCtx])
  return (
    <SnackbarContent
      className={clsx(classes[variant], classes.customSnackbar)}
      aria-describedby="client-snackbar"
      message={
        <span id="client-snackbar" className={classes.message}>
          {variant === 'error' ? (
            <i className="error-icon" />
          ) : (
            <Icon className={clsx(classes.icon, classes.iconVariant)} />
          )}
          {message}
        </span>
      }
      action={[
        <IconButton
          key="close"
          aria-label="close"
          color="inherit"
          onClick={() => {
            mutationCtx.removeTop()
          }}
        >
          <CloseIcon className={clsx(classes.icon)} />
        </IconButton>
      ]}
      {...other}
    />
  )
}

export default function CustomizedSnackbar (props) {
  const handleClose = (evt) => {}
  return (
    <>
      {props.toasts.map((item, index) => (
        <Snackbar
          key={index}
          open={true}
          anchorOrigin={{
            vertical: 'top',
            horizontal: 'center'
          }}
          // onClose={handleClose}
        >
          <SnackbarWrapper
            onClose={handleClose}
            variant={item.variant}
            message={`${item.message}`}
          />
        </Snackbar>
      ))}
    </>
  )
}
