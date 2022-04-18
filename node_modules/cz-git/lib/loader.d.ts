/**
 * @description: generate commitizen config option(generateOptions) | generate commitizen questions(generateQuestions)
 * @author: @Zhengqbbb (zhengqbbb@gmail.com)
 * @license: MIT
 */
import { UserConfig } from "./share";
import type { Answers, CommitizenGitOptions } from "./share";
export declare const generateOptions: (clConfig: UserConfig) => CommitizenGitOptions;
export declare const generateQuestions: (options: CommitizenGitOptions, cz: any) => false | ({
    type: string;
    name: string;
    message: string | undefined;
    default: string | import("./share").StringCallback | undefined;
    when(answers: Answers): boolean;
    transformer?: undefined;
    source?: undefined;
    choices?: undefined;
} | {
    type: string;
    name: string;
    message: string | undefined;
    validate(subject: string, answers: Answers): string | boolean;
    transformer: (subject: string, answers: Answers) => string;
    filter(subject: string): string;
    default: string | import("./share").StringCallback | undefined;
    source?: undefined;
    choices?: undefined;
} | {
    type: string;
    name: string;
    message: string | undefined;
    default: string | import("./share").StringCallback | undefined;
    transformer?: undefined;
    source?: undefined;
    choices?: undefined;
} | {
    type: string;
    name: string;
    message: string | undefined;
    source: (_: Answers, input: string) => {
        name: string;
        value: any;
    }[];
    default?: undefined;
    transformer?: undefined;
    choices?: undefined;
} | {
    type: string;
    name: string;
    choices: {
        key: string;
        name: string;
        value: string;
    }[];
    default: number;
    message(answers: Answers): string | undefined;
    transformer?: undefined;
    source?: undefined;
})[];
declare type GenerateQuestionsType = typeof generateQuestions;
export declare type QuestionsType = ReturnType<GenerateQuestionsType>;
export {};
