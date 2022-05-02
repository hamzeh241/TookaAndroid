package ir.tdaapp.tooka.models.util

/**
 * Enum haye marbut be waziat javab hai ke az Server dariaft mishavad
 * @param NETWORK_ERROR Hengami call mishavad ke internet kond ya ghat bashad
 * @param CLIENT_ERROR Hengami call mishavad ke dadeie eshtebah be samt Server ersal shavad
 * @param NOT_FOUND_ERROR Hengami call mishavad ke Server javabi motanaseb ba khaste app peida nakarde ast
 * @param SERVER_ERROR Hengami call mishavad ke dar code haie Server khatai bashad
 * @param UNAUTHORIZED_ERROR Hengami call mishavad ke Authentication beine Server o Client bargharar nabashad
 * @param UNKNOWN_ERROR Hengami call mishavad ke khataii namoshakhas rokh dade bashad
 */
enum class NetworkErrors {
  NETWORK_ERROR,
  CLIENT_ERROR,
  NOT_FOUND_ERROR,
  SERVER_ERROR,
  UNAUTHORIZED_ERROR,
  UNKNOWN_ERROR
}