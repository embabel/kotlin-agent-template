#!/usr/bin/env bash

export AGENT_APPLICATION=..
export SPRING_PROFILES_ACTIVE=web,severance

./support/agent.sh
