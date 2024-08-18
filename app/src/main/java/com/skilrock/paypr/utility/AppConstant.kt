package com.skilrock.paypr.utility

import com.skilrock.paypr.BuildConfig


const val LOGIN_TOKEN                   = "infinity"
const val DEVICE_TYPE                   = "MOBILE"
const val LOGIN_DEVICE                  = "ANDROID_APP_CASH"
const val REQUEST_IP                    = "127.0.0.1"
const val MERCHANT_ID_INFINITI          = "INFINITI"
const val PLAYER                        = "Player"
const val CONSUMER                      = "Consumer"
const val RETAILER                      = "Retailer"
const val MERCHANT                      = "Merchant"
const val BUILD_TYPE_UAT                = "UAT"
const val CONTENT_TYPE                  = "Content-Type: application/json"
const val LOGIN_URL                     = "Weaver/service/rest/playerLogin"
const val VERSION_URL                   = "Weaver/service/rest/versionControl"
const val FORGOT_PASSWORD_URL           = "Weaver/service/rest/forgotPassword"
const val RESET_PASSWORD_URL            = "Weaver/service/rest/resetPassword"
const val REGISTRATION_URL              = "Weaver/service/rest/v1/playerRegistration"
const val REGISTRATION_OTP_URL          = "Weaver/service/rest/registrationOTP"
const val MY_TRANSACTIONS_URL           = "Weaver/service/rest/transactionDetails"
const val CHANGE_PASSWORD_URL           = "Weaver/service/rest/changePassword"
const val UPDATE_PROFILE_URL            = "Weaver/service/rest/updatePlayerProfile"
const val UPLOAD_AVATAR_URL             = "Weaver/service/rest/uploadAvatar"
const val UPDATE_BALANCE_URL            = "Weaver/service/rest/getBalance"
const val FUND_TRANSFER_URL             = "Weaver/service/rest/ola/txn/FundsTransferFromWallet"
const val LOGOUT_URL                    = "Weaver/service/rest/playerLogout"
const val REGISTRATION_VALIDATION_URL   = "Weaver/service/rest/checkAvailability"
const val LAST_LOGIN_URL                = "Weaver/service/rest/fetch/loginDetails"
const val MERCHANT_LOCATION_URL         = "${BuildConfig.RMS_BASE_URL}RMS/clients/getLocationWiseRetailer"

const val SESSION_EXPIRE_CODE           = 203
const val PARSE_TYPE                    = "text/plain"
const val RMS_CLIENT_TOKEN              = "M1p3FjmsKnswEKA501MCHCMjdqbdar9k1FoXxP5wb7k"

const val PERMISSIONS_REQUEST_READ_CONTACTS     = 100
const val NOTIFICATION_CHANNEL_TRANSACTIONAL    = "TRANSACTIONAL"
const val NOTIFICATION_CHANNEL_ALERT            = "ALERT"
const val NOTIFICATION_CHANNEL_PROMOTIONAL      = "PROMOTIONAL"