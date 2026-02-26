#pragma once
#define NOMINMAX
#define WIN32_LEAN_AND_MEAN


#include <iostream>
#include <thread>
#include <chrono>
#include <curl/curl.h>
#include <nlohmann/json.hpp>



using json = nlohmann::json;
size_t WriteCallback(void* contents, size_t size, size_t nmemb, std::string* s);

std::string httpGET(const std::string& url);
std::string httpPOST(const std::string& url, const std::string& jsonBody);
void asyncGetLoop();