package org.example.project.core.data

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import org.example.project.core.domain.DataError
import org.example.project.core.domain.Result
import kotlin.coroutines.coroutineContext


//Utility functions for our growing app that could need to deal with multiple API endpoints
// from ktor: HttpResponse.status typed as HttpStatusCode{description: String, value: Int}

suspend inline fun <reified T>responseToResult(
    response: HttpResponse,
): Result<T, DataError.Remote>{
    return when(response.status.value){
        in 200..299 -> { //HttpStatusCode starts with 2 -> OK
            try{
                Result.Success(data = response.body<T>())
            }catch(e: NoTransformationFoundException){
                Result.Error(error = DataError.Remote.SERIALIZATION)
            }
        }
        408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Remote.SERVER)
        else -> Result.Error(DataError.Remote.UNKNOWN)
    }
}


//ensure getting a valid API/http response
suspend inline fun <reified T>safeCall(
    execute: ()-> HttpResponse
) : Result<T, DataError.Remote>
{
    val response = try {
        execute()
    } catch(e: SocketTimeoutException ){
        return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
    } catch(e: UnresolvedAddressException){
        return Result.Error(DataError.Remote.NO_INTERNET)
    }catch(e: Exception){
        coroutineContext.ensureActive() // If cancellation exception thrown by suspend function is catched such as "execute()" -> break this cancellation as the parent coroutine may not notified
        return Result.Error(DataError.Remote.UNKNOWN)
    }
    return responseToResult(response)
}