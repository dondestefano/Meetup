const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.notifyNewFriend = functions.firestore
.document('friendRequests/{user}/requests/{newRequest}')
.onCreate((docSnapshot, context) => {
    const request = docSnapshot.data();
    const user = context.params.user;
    const friend = request['from'];
    const recipientId = request['to'];

    // Only send a notification to the recipient
    if (user === recipientId) {
        return admin.firestore().doc('users/' + recipientId).get().then(userDoc => {
            const registrationTokens = userDoc.get('registrationTokens');
        
    
            const notificationBody = ("Someone wants to be your friend.");
            const payload = {
                data: {
                    sender: "friend",
                    friend: friend
                }
            }
    
            return admin.messaging().sendToDevice(registrationTokens, payload).then( response => {
                const stillRegisteredTokens = registrationTokens
                // Check if tokens are no longer registered and delete those who aren't.
                response.results.forEach((result, index) => {
                    const error = result.error
                    if (error) {
                        const failedRegistrationToken = registrationTokens[index]
                        if (error.code === 'messaging/invalid-registration-token'
                            || error.code === 'messaging/registration-token-not-registered') {
                                const failedIndex = stillRegisteredTokens.indexOf(failedRegistrationToken)
                                if(failedIndex > -1) {
                                    stillRegisteredTokens.splice(failedIndex, 1)
                                }
                        }
                    }
                })
                return admin.firestore().doc('users/' + recipientId).update({
                    registrationTokens: stillRegisteredTokens
                })
            })
        })
    } else {return null}
})

exports.notifyNewEvent = functions.firestore
.document('events/{user}/userEvents/{event}')
.onCreate((docSnapshot, context) => {
    const event = docSnapshot.data();
    const host = event['host'];
    const recipientId = context.params.user;
    const eventName = event['name'];

    if (recipientId !== host) {
        return admin.firestore().doc('users/' + recipientId).get().then(userDoc => {
            const registrationTokens = userDoc.get('registrationTokens');
    
            const notificationBody = ("You have been invited. Are you game?");
            const payload = {
                data: {
                    sender: "event",
                    hostId: host,
                    eventName: eventName
                }
            }
    
            return admin.messaging().sendToDevice(registrationTokens, payload).then( response => {
                const stillRegisteredTokens = registrationTokens
                // Check if tokens are no longer registered and delete those who aren't.
                response.results.forEach((result, index) => {
                    const error = result.error
                    if (error) {
                        const failedRegistrationToken = registrationTokens[index]
                        if (error.code === 'messaging/invalid-registration-token'
                            || error.code === 'messaging/registration-token-not-registered') {
                                const failedIndex = stillRegisteredTokens.indexOf(failedRegistrationToken)
                                if(failedIndex > -1) {
                                    stillRegisteredTokens.splice(failedIndex, 1)
                                }
                            }
                    }
                })
                return admin.firestore().doc('users/' + recipientId).update({
                    registrationTokens: stillRegisteredTokens
                })
            })
        })
    }
})
