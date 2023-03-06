package com.noljanolja.server.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.noljanolja.server.core.model.User
import com.noljanolja.server.core.model.UserContact
import com.noljanolja.server.core.repo.user.*
import com.noljanolja.server.core.repo.user.UserModel.Companion.toUserModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class UserService(
    private val userRepo: UserRepo,
    private val userContactsRepo: UserContactsRepo,
    private val contactsRepo: ContactRepo,
    private val objectMapper: ObjectMapper,
) {
    private val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    suspend fun getUsers(
        page: Int,
        pageSize: Int,
    ): Pair<List<User>, Int> = coroutineScope {
        val total = async {
            userRepo.count()
        }
        val users = async {
            userRepo.findAll(
                offset = ((page - 1) * pageSize),
                limit = pageSize,
            ).map { it.toUser(objectMapper) }
        }
        Pair(
            users.await(),
            total.await().toInt(),
        )
    }

    suspend fun getUser(
        userId: String,
    ): User? = userRepo.findById(userId)?.toUser(objectMapper)

    suspend fun upsertUser(
        user: User
    ): User = userRepo.save(user.toUserModel(objectMapper)).toUser(objectMapper)

    suspend fun deleteUser(
        userId: String,
    ) {
        userRepo.softDeleteById(userId)
    }

    suspend fun getUserContacts(
        userId: String,
    ): List<UserContact> {
        // TODO get and map user contact
    }

    suspend fun upsertUserContacts(
        userId: String,
        userContacts: List<UserContact>
    ) {
        // Get existing user contacts
        val existingUserContacts = userContactsRepo.findAllByUserId(userId).toList()
        // Get exising user contact details
        val existingContacts = contactsRepo.findAllById(existingUserContacts.map { it.contactId }).toList()
        // New contacts to be saved
        val newContacts = mutableMapOf<Int, ContactModel>()
        val newUserContacts = mutableMapOf<Int, UserContactModel>()
        // Existing contacts to be updated
        val updateUserContacts = mutableListOf<UserContactModel>()
        userContacts.forEachIndexed { index, userContact ->
            // parse phone number
            val phoneNumber = try {
                phoneNumberUtil.parse(userContact.phone, null)
            } catch (error: NumberParseException) {
                null
            }
            // only check contact which has valid email or phone number
            // TODO check email
            if (userContact.email == null && phoneNumber == null) {
                return@forEachIndexed
            }
            // find an existing contact which has the same email or number
            val existingContact = existingContacts.find {
                (it.email != null && it.email == userContact.email) ||
                        (it.phoneNumber != null && it.phoneNumber == phoneNumber?.nationalNumber.toString())
            }
            if (existingContact == null) {
                // Contact does not exist -> will be added
                newContacts[index] = ContactModel(
                    id = 0,
                    countryCode = phoneNumber?.countryCode?.toString(),
                    phoneNumber = phoneNumber?.nationalNumber?.toString(),
                    email = userContact.email,
                )
                newUserContacts[index] = UserContactModel(
                    id = 0,
                    userId = userId,
                    contactId = 0,
                    contactName = userContact.name,
                )
            } else {
                // Contact exists -> check if contact name has been changed -> will be updated
                val existingUserContact = existingUserContacts.first { it.contactId == existingContact.id }
                if (userContact.name.isNotBlank() && userContact.name != existingUserContact.contactName) {
                    updateUserContacts.add(existingUserContact.copy(contactName = userContact.name))
                }
            }
        }
        if (newContacts.isNotEmpty()) {
            // Save new contacts
            // TODO find a way to save all
            newContacts.forEach { (index, contact) ->
                val savedContact = contactsRepo.save(contact)
                userContactsRepo.save(newUserContacts[index]!!.copy(contactId = savedContact.id))
            }
        }
        if (updateUserContacts.isNotEmpty()) {
            // Update existing contacts
            userContactsRepo.saveAll(updateUserContacts).toList()
        }
    }
}