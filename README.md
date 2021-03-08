# Two-Layer Encrypted Contact Log (TECL)

**Disclaimer: This project is a proof of concept. The code, as well as the concept, are not suited for production use.**
**The protocol and reference implementation require further inspection!**


## Context And Motivation
Various solutions exist to trace contacts in case of infectious diseases. Tracing in the background like the Exposure Notification protocol by Apple and Google allows privacy-preserving contact-tracing on a voluntary basis.
However, while the health authorities might represent a trusted party (as they verify infections and publish e.g. daily keys in the EN protocol), their insights are limited by the same measures that protect the privacy of users.
As a simple consequence, other means of tracing are issued by the government. For example, in Germany, venues such as restaurants are required to log a customer's name and address.
Using pen and paper is a reliable and widely applicable solution and provides the health authorities with crucial contact information.
At the same time, e.g. customers have to entrust each venue with their personal details, which could lead to falsified or completely imaginary contact information.

The main disadvantages of pen and paper solutions are two-fold:
1. Private information is disclosed even if there was retrospectively no need for it
2. Third parties (such as venue owners) get unnecessary access to this data


This protocol concept aims to solve both issues by first disclosing the contact information only if needed (e.g. a verified infection) and second by removing the third party from the contact information flow.
The result could be a contact log that is more trusted and further requires less administration by venue owners; this could further increase acceptance (and lower false contact information).

## Overview
This concept builds on the following design choices:
* Treat health authorities as trusted entities
* Contact information should be only available to the health authorities and only if at least one party provides access (i.e. in case of an infection)
* Target convenient usage with support for NFC and pre-generated QR-codes
* Make no assumptions about the actual type of contact information (e.g. just name with a telephone number or even just an anonymous communication channel identifier)

This concept aims to be an open-source and interoperable protocol base. The rough idea is to encrypt information in two layers:
The first layer allows only the health authorities to decrypt and read the information; this information is shared with e.g. the venue.
The second layer encrypts this information again and prevents the health authorities from directly decrypting the contact information unless one party reveals its personal decryption key.
With the consent of an involved party, the health authority can decrypt both layers and read the logged contact information.


## Concept

We assume that used cryptographic primitives are reasonably secure.
The protocol builds on the concept of crypto boxes, i.e. cryptographically secured "boxes" of data.
Overall, the protocol constitutes of three types of boxes: symmetric (SecretBox), asymmetric with the known sender (CryptoBox), and asymmetric with an unknown sender (SealedBox).
All boxes allow a legitimate receiver to validate the integrity of the message, i.e. a correct receiver can confirm that the message was not modified during transport.
The boxes, however, differ in the way their content can be decrypted and whether the identity of the sender is known.

The most basic box is the SecretBox which provides symmetric encryption and decryption using the same secret.
A SecretBox can thus be opened (or decrypted) by every entity that knows the secret; it is not addressed to any unique party and further does not contain any information about the sender.
In comparison, the "normal" CryptoBox asymmetrically encrypts content for a specific party from a specific sender; only the targeted receiver is able to decrypt the information for which the identity of the sender is also required.
The SealedBox also encrypts information for a specific receiver but does not require the sender's identity to be known.

The protocol uses all three kinds of boxes. We will now explore the different design choices of the protocol and add the different types of boxes to serve different purposes in the design.

### Protocol Design

Let $A$, $B$, and $H$ be the public keys of Alice, Bob, and the Health Authority with respective private keys $a$, $b$, and $h$.
(Actually $A$ and $B$ should use fresh keys for each new encounter)
Moreover, let $contact_A$ and $contact_B$ be the contact information of A and B and let $additionalData_A$, $additionalData_B$ be additional data that should be available for the other parties (thus not targeting the health authority).
Let $encounterData$ be the encounter-specific data such as start- and end-time.

Assume that party $A$ scans the code of $B$.

#### Hiding Contact Information From the Involved Parties
While it might be okay to share your name with the other parties, there is less need to also disclose, e.g. your address.
We thus encrypt all personal contact information, so that only the health authority is able to decrypt it (using $H$).
As we want the health authority to confirm the source of contact information, we use the CryptoBox and add the sender identity.
If, for example, $B$ shares information with $A$, $B$ would transfer: $(B, CryptoBox(contact_B), $additionalData_B$)$.
To create the CryptoBox, $B$ needs access to his private Key $b$ as well as the public key $H$ of the health authority.
$B$ can share this information with $A$ using e.g. a simple QR code.
The additional Data could contain e.g. the name of the venue which $A$ could display to the user.

#### Secure Upload
$B$ could share information with $A$ using a QR-code that might also be generated in advance.
Therefore, $B$ could not be able to receive or store the contact information of $A$.
This is solved by $A$ uploading the relevant contact data to a storage server.

To secure the upload, $A$ randomly generates a fresh symmetric key $sk$ and encrypts the information of both $A$ and $B$ including $encounterData$ with it: $SecretBox([(A, CryptoBox(contact_A)), (B, CryptoBox(contact_B))], encounterData)$.
This way, the data is only readable with access to $sk$. To allow contact tracing, all parties need to be able to reveal the (encrypted) contact information which corresponds to revealing $sk$ to the server (which would be at best but not necessarily operated by the health authorities as well).
Because $A$ has no way to send $sk$ to $B$ right now, it asymmetrically encrypts the encounter key $sk$ for $B$ and uploads it as well: $(B, SealedBox(sk))$
This time in a SealedBox to hide the sender information (in this case $A$).
$A$ would also do the same thing for itself (and effectively all other parties) encrypting and uploading $sk$. This way, each party of the encounter can reveal their shared key $sk$ with their private keys.
The $encounterData$ would be set by $A$ and could contain the start time and duration.

#### Hiding Upload patterns
As there is exactly one $SealedBox$ upload for every party and one symmetrically encrypted party entry, $A$ thus combines each of them to one upload "pair". $A$ can further delay the upload of pairs thus mixing upload pairs of different encounters to hide specific encounters.
In addition, the uploader $A$ could generate and upload fake data (please also see "Efficient Search in the Data").
Ultimately another proxy server could collect and mix data to prevent user identification based on e.g. their IP or fingerprinting.
We require that at least on $SecretBox$ entry also contains $encounterData$.

#### Tracing

Assume that party $B$ got infected and shall now reveal contact details to the health authorities.
$B$ would then reveal the relevant private keys that correspond to the used public keys, e.g. $B$ during the relevant times.

In the simplest case, the upload server is operated by the health authorities themselves which hides e.g. access patterns.
With the private key $b$, the health authority could reveal the shared key $sk$ and with it, the information $([(A, CryptoBox(contact_A)), (B, CryptoBox(contact_B))], encounterData)$.
The health authority would then be able to read of course the $encounterData$ but it could also verify and decrypt the crypto boxes which had previously been asymmetrically encrypted using $H$; the health authority thus has access to the contact information.


#### Efficient Search in the Data
In the case of the reception of private keys, the health authorities deal with probably an enormous database with possibly billions of entries. It will therefore be infeasible to linearly search through the entries trying to decrypt them individually.
Using the public key, a lookup of the corresponding SealedBox entry is possible.
Because the protocol uploads pairs $((B, SealedBox(sk)),SecretBox(((A, CryptoBox(contact_A)), encounterData)))$, the first SecretBox entry is luckily known and as such, the public key $A$ could be revealed.
If $A$ now orders the parties into a ring (e.g. B->A->B) such that each decryption reveals the next possible entry, the decryption process efficiency is increased.
Lookup based on the public key is a simple

## Reference Implementation

Our test implementation (directory "server") is based on [LazySodium](https://github.com/terl/lazysodium-java) / LibSodium which provides the mentioned crypto boxes:
* [SecretBox](https://libsodium.gitbook.io/doc/secret-key_cryptography/secretbox)
* [Box](https://libsodium.gitbook.io/doc/public-key_cryptography/authenticated_encryption)
* [Sealed](https://libsodium.gitbook.io/doc/public-key_cryptography/sealed_boxes)


## Questions and Answers
The answers should be seen as "in progress" until the protocol has been thoroughly analyzed.


#### What if a party directly reveals the received information?
Only the health authority would be able to decrypt the contained details.
No third party would be able to read it.


#### Can the other party redistribute my information?
Yes, the other party could redistribute your (encrypted) information.
The same malicious behavior is possible with solutions like pen and paper.
TODO: Protocol extensions could introduce a validity period.

#### Why is the CryptoBox that contains the contact information not also signed using the shipped identity?
As the other parties are not able to decrypt the content, a malicious party could simply generate a new (public, private) key pair and sign e.g. a replayed (encrypted contact) information.
Only the health authority would be able to verify the origins. TODO: Is this true?

#### Could the user also log-out from venues?
Checking out from a location could be supported using additional data in either $encounterData$ or the contact data e.g. $contact_B$ (which could be a second QR code).


#### Could users verify the public key while scanning?
If they have a two-way data exchange using e.g. NFC, they could ask the other user to sign a Nonce to prove its identity.
For now, only the health authority is able to verify the contact info.


#### How much storage does one encounter require?
In the test implementation, one party of an encounter generates about 1 kB of data.
For an encounter of n parties, n kB would thus be generated.


#### Do I need to trust the uploader?
The uploader could potentially add other contacts to the group that were not actually present.
Most of the time, customers are the ones who actively scan (static) QR codes of venues.


#### What if I do not have access to a smartphone or smartwatch?
QR codes could be printed in advance which contain encrypted contact info (possibly one side for check-in plus checkout and another one for the private key).
The Fallback solution (pen and paper) is in every case still possible.

#### Why is the contact data not separately saved (to save e.g. storage)?
As contact data is not likely to change between different encounters, contact data could be also stored symmetrically encrypted and when tracing an encounter, the symmetric key could be revealed.
This variant however requires that the parties upload their data without encounters (the uploader of a group could also upload this data but this could make multiple encounters connectable again).


#### How is data secured against loss?
Data-loss is an imminent thread for the protocol. While the encrypted entries are uploaded, they are not useful if an infected party has lost access to private keys.


#### How is the contact info verified?
The protocol itself does not verify any data.
However, providing apps could extend the protocol to support e.g. phone number or ID verification.


#### Preventing attacks based on the length of entries
As the encryption does not hide the length of entries it could potentially make entries linkable (with e.g. unique lengths).
We, therefore, pad the custom data in the entries to the very same length.
Note that this unpadded data could still be zero in length which is useful for the creation of dummy entries.
Data is padded to the next X Bytes for all custom data (based on the underlying encryption mechanism).
E.g. this could mean for Salsa20 to pad to the next (64 Bytes) 512 bits.
Note that the padding is applied after encoding the real size using 4 bytes (e.g. of 64 bytes, up to 60 are usable for content).

#### Dummy Data
In some cases, users want to upload dummy data to hide real contact behavior (which might still get analyzed through e.g. upload behavior). While data shuffling lowers the risk, dummy data could further hide the individual amount of encounters.

#### Data Retention
The uploaded entries should be combined with timestamps and should only be retained for the relevant amount of time.
Users that like to share their contacts do also just have the incentive to publish only their relevant keys (e.g. for a specific period of time rather than their entire upload history).

#### Support for temporal keys of health authorities
We encourage usage of changing keys of health authorities (e.g. they could also change daily).

## TODO
* Try to formally verify the protocol
* Add example Android app implementation
