import { Answers, CommitizenGitOptions } from "../share";
export declare const editCommit: (answers: Answers, options: CommitizenGitOptions, cb: (message: string) => void) => void;
export declare const getPreparedCommit: (context: string) => string | null;
