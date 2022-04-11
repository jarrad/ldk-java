# LDK

Some simple constructs for implementing a Java based lightning node using LDK.

## Mempool.Space Client

A client interface the provides access to
the [Mempool.space Rest API](https://mempool.space/docs/api/rest).

## FeeEstimator

`MempoolSpaceFeeEstimator` that uses
the [Mempool.space Rest API](https://mempool.space/docs/api/rest) to estimate fees.

Also provides a `InMemoryCachingFeeEstimator` that will cache the results of fees
by `ConfirmationTarget` in order to save network calls to load the current fees.



