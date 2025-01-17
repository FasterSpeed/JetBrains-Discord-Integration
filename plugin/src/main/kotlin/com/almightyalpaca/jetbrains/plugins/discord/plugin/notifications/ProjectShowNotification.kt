/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.ProjectShow
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Plugin
import com.intellij.notification.*
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val title = "Pokazywać Discord RPC w projekcie?"
private const val content =
    "Kliknij czy ma pokazywać discord RPC. Jak chcesz to offnąć wejdź w  Settings > Other Settings > Discord > Project"

private val group = NotificationGroup(
    "${Plugin.getId()}.project.show",
    NotificationDisplayType.STICKY_BALLOON,
    true
)

object ProjectShowNotification {
    suspend fun show(project: Project) = suspendCoroutine<ProjectShow> { continuation ->
        group.createNotification(title, null, content, NotificationType.INFORMATION)
            .apply {
                collapseActionsDirection = Notification.CollapseActionsDirection.KEEP_LEFTMOST
                for (value in project.settings.show.selectableValues) {
                    addAction(DumbAwareAction.create(value.text) {
                        expire()
                        continuation.resume(value)
                    })
                }
            }.run { Notifications.Bus.notify(this, project) }
    }
}
