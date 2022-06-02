package ir.tdaapp.tooka.models.util

enum class UserErrors {
  DATABASE_EXCEPTION,
  SMS_EXCEPTION,
  INVALID_ARGS_EXCEPTION,
  NOT_FOUND,
  NETWORK_EXCEPTION,
  INVALID_CODE,
  CODE_TOO_EARLY_TO_SEND,
  UNKNOWN_EXCEPTION
}